package com.school.service;

import com.school.dto.DueTrackingDto;
import com.school.entity.DueTracking;
import com.school.entity.Lending;
import com.school.repository.DueTrackingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DueTrackingServiceTest {

    @Mock
    private DueTrackingRepository dueTrackingRepository;

    @InjectMocks
    private DueTrackingService dueTrackingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActiveAndUpcomingDueTracking() {
        LocalDateTime cutoff = LocalDateTime.now().plusDays(7);
        DueTracking dt1 = new DueTracking();
        dt1.setDueId(1L);
        DueTracking dt2 = new DueTracking();
        dt2.setDueId(2L);

        when(dueTrackingRepository.findByReturnDateIsNullAndDueDateBefore(cutoff))
                .thenReturn(Arrays.asList(dt1, dt2));

        List<DueTracking> result = dueTrackingService.getActiveAndUpcomingDueTracking(cutoff);

        assertEquals(2, result.size());
        verify(dueTrackingRepository, times(1)).findByReturnDateIsNullAndDueDateBefore(cutoff);
    }

    @Test
    void testGetAllDueTracking() {
        DueTracking dt1 = new DueTracking();
        dt1.setDueId(1L);
        dt1.setLendingId(10L);
        dt1.setOverdue(false);
        DueTracking dt2 = new DueTracking();
        dt2.setDueId(2L);
        dt2.setLendingId(11L);
        dt2.setOverdue(true);

        when(dueTrackingRepository.findAll()).thenReturn(Arrays.asList(dt1, dt2));

        List<DueTrackingDto> result = dueTrackingService.getAllDueTracking();

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getLendingId());
        assertTrue(result.get(1).getOverdue());
        verify(dueTrackingRepository, times(1)).findAll();
    }

    @Test
    void testUpdateDueDate_NewRecord() {
        Lending lending = new Lending();
        lending.setLendingId(1L);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);

        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.empty());
        when(dueTrackingRepository.save(any(DueTracking.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> dueTrackingService.updateDueDate(lending, dueDate));

        verify(dueTrackingRepository, times(1)).save(any(DueTracking.class));
    }

    @Test
    void testUpdateReturnDate_Success() {
        DueTracking dt = new DueTracking();
        dt.setLendingId(1L);

        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.of(dt));
        when(dueTrackingRepository.save(dt)).thenReturn(dt);

        assertDoesNotThrow(() -> dueTrackingService.updateReturnDate(1L));
        assertNotNull(dt.getReturnDate());
        verify(dueTrackingRepository, times(1)).save(dt);
    }

    @Test
    void testUpdateReturnDate_NotFound() {
        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> dueTrackingService.updateReturnDate(1L));

        assertEquals("Due tracking record not found for loan.", ex.getMessage());
    }

    @Test
    void testUpdateRejectionDate_Success() {
        DueTracking dt = new DueTracking();
        dt.setLendingId(1L);

        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.of(dt));
        when(dueTrackingRepository.save(dt)).thenReturn(dt);

        assertDoesNotThrow(() -> dueTrackingService.updateRejectionDate(1L));
        assertNotNull(dt.getRejectionDate());
        verify(dueTrackingRepository, times(1)).save(dt);
    }

    @Test
    void testUpdateRejectionDate_NotFound() {
        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> dueTrackingService.updateRejectionDate(1L));

        assertEquals("Due tracking record not found for loan.", ex.getMessage());
    }

    @Test
    void testClearPreviousDueDates_NewRecord() {
        when(dueTrackingRepository.findByLendingId(1L)).thenReturn(Optional.empty());
        when(dueTrackingRepository.save(any(DueTracking.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> dueTrackingService.clearPreviousDueDates(1L));
        verify(dueTrackingRepository, times(1)).save(any(DueTracking.class));
    }
}
