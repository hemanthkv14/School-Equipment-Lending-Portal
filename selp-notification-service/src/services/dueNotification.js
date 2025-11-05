import  pool  from '../config/db.js';

export const getUserDueLendings = async (userId) => {
    const query = `
    SELECT l.lending_id, d.due_date
    FROM duetracking d
    INNER JOIN lendings l ON d.lending_id = l.lending_id
    WHERE l.borrower_id = $1
      AND d.due_date IS NOT NULL
    ORDER BY d.due_date ASC
  `;

    const result = await pool.query(query, [userId]);
    return result.rows; // returns array of { lending_id, due_date }
};
