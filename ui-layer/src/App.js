import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import EquipmentList from "./components/EquipmentList";
import RequestForm from "./components/RequestForm";
function App() {
  return (
     <Router>
            <Routes>
                <Route path="/" element={<EquipmentList />} />
                <Route path="/new-request/:equipmentId" element={<RequestForm />} />
            </Routes>
        </Router>
  );
}

export default App;
