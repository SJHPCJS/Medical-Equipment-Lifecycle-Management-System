// Mock data for Equipment Manager area

export const deviceStatuses = ['Unassigned', 'In Use', 'Under Repair', 'Disabled', 'Pending Scrap']

export const devices = [
  { id: 'EQ-0001', type: 'MRI Scanner', status: 'In Use', departmentId: 'DPT-004', vendor: 'MedTech Co.', description: '3.0T high-field MRI for neuro and musculoskeletal imaging. Fast sequences with low noise.', diseases: ['Stroke', 'Brain tumor', 'Multiple sclerosis'] },
  { id: 'EQ-0002', type: 'X-Ray Machine', status: 'Under Repair', departmentId: 'DPT-004', vendor: 'HealthCorp', description: 'Digital radiography system with flat-panel detector, suitable for chest and skeletal exams.', diseases: ['Pneumonia', 'Fracture', 'COPD'] },
  { id: 'EQ-0003', type: 'Ultrasound', status: 'Unassigned', departmentId: '', vendor: 'SonoPlus', description: 'Portable ultrasound with color Doppler and cardiac presets, lightweight for bedside scanning.', diseases: ['Gallstones', 'Thyroid nodules', 'DVT'] },
  { id: 'EQ-0004', type: 'ECG Monitor', status: 'In Use', departmentId: 'DPT-001', vendor: 'Pulse Ltd.', description: '12-lead ECG monitor with arrhythmia detection and long-term trending.', diseases: ['Arrhythmia', 'Myocardial infarction risk', 'Electrolyte imbalance'] },
  { id: 'EQ-0005', type: 'Ventilator', status: 'Pending Scrap', departmentId: 'DPT-006', vendor: 'AirLife', description: 'ICU-grade ventilator nearing end of lifecycle, limited ventilation modes.', diseases: ['ARDS', 'COPD exacerbation', 'Respiratory failure'] },
  { id: 'EQ-0006', type: 'Infusion Pump', status: 'Disabled', departmentId: 'DPT-002', vendor: 'CareFlow', description: 'Volumetric infusion pump, precise micro-infusion control; currently disabled for safety check.', diseases: ['Sepsis', 'Dehydration', 'Chemotherapy administration'] },
]

export const todayTickets = {
  pendingResponse: 3,
  inProgress: 6,
  pendingAcceptance: 2,
}

export const receivingOrders = [
  { id: 'PO-2025-001', type: 'ECG Monitor', quantity: 10, requester: 'Procurement Team' },
  { id: 'PO-2025-002', type: 'Ultrasound', quantity: 3, requester: 'Cardiology' },
]

export const repairTickets = [
  { id: 'R-0001', createdAt: '2025-09-10T09:10:00Z', finishedAt: '', notes: '', cost: 0, result: '', status: 'Pending Review', department: 'Radiology' },
  { id: 'R-0002', createdAt: '2025-09-11T10:20:00Z', finishedAt: '', notes: 'Noise issue identified', cost: 120, result: '', status: 'In Repair', department: 'Emergency' },
  { id: 'R-0003', createdAt: '2025-09-09T08:30:00Z', finishedAt: '2025-09-11T17:00:00Z', notes: 'Replaced fan', cost: 80, result: 'Fixed', status: 'Completed', department: 'Cardiology' },
]

export const scrapApplications = [
  { id: 'S-0001', deviceId: 'EQ-0005', reason: 'Beyond economic repair', status: 'Pending', record: '', photoUrl: '' },
  { id: 'S-0002', deviceId: 'EQ-0012', reason: 'Obsolete', status: 'Pending', record: '', photoUrl: '' },
]

export function clone(value) { return JSON.parse(JSON.stringify(value)) }



