// Mock data for Department User area

export const usageTodos = [
  { id: 'U-0001', deviceId: 'EQ-0004', pending: true },
  { id: 'U-0002', deviceId: 'EQ-0001', pending: true },
]

export const repairConfirmTodos = [
  { id: 'RC-0001', ticketId: 'R-0003', deviceId: 'EQ-0002' },
]

export const deptDevices = [
  { id: 'EQ-0001', type: 'ECG Monitor', status: 'In Use', vendor: 'Pulse Ltd.', departmentId: 'DPT-001', description: 'Bedside ECG monitor with 12-lead support and alarm notifications.', diseases: ['Arrhythmia', 'Ischemia monitoring'] },
  { id: 'EQ-0004', type: 'Ultrasound', status: 'In Use', vendor: 'SonoPlus', departmentId: 'DPT-001', description: 'Cart-based ultrasound for abdominal and vascular scans.', diseases: ['Gallstones', 'DVT'] },
  { id: 'EQ-0007', type: 'Infusion Pump', status: 'Under Repair', vendor: 'CareFlow', departmentId: 'DPT-001', description: 'Smart infusion system; undergoing pump calibration.', diseases: ['Sepsis', 'Chemotherapy administration'] },
]

export const usageLogs = [
  // EQ-0001 (ECG Monitor) logs
  { id: 'UL-0001', deviceId: 'EQ-0001', start: '2025-09-10T09:00:00Z', end: '2025-09-10T10:00:00Z', purpose: 'Routine check', notes: '', exception: '' },
  { id: 'UL-0002', deviceId: 'EQ-0001', start: '2025-09-10T11:15:00Z', end: '2025-09-10T11:45:00Z', purpose: 'Patient monitoring', notes: 'Stable vitals', exception: '' },
  { id: 'UL-0003', deviceId: 'EQ-0001', start: '2025-09-11T09:15:00Z', end: '2025-09-11T09:45:00Z', purpose: 'ECG baseline', notes: '', exception: '' },
  { id: 'UL-0004', deviceId: 'EQ-0001', start: '2025-09-12T08:00:00Z', end: '2025-09-12T08:30:00Z', purpose: 'Pre-op check', notes: 'No anomalies', exception: '' },

  // EQ-0004 (Ultrasound) logs
  { id: 'UL-0005', deviceId: 'EQ-0004', start: '2025-09-10T14:00:00Z', end: '2025-09-10T14:30:00Z', purpose: 'Abdominal scan', notes: 'Clear image', exception: '' },
  { id: 'UL-0006', deviceId: 'EQ-0004', start: '2025-09-11T16:15:00Z', end: '2025-09-11T16:50:00Z', purpose: 'Echo training', notes: '', exception: 'Image jitter observed' },
  { id: 'UL-0007', deviceId: 'EQ-0004', start: '2025-09-12T10:05:00Z', end: '2025-09-12T10:25:00Z', purpose: 'Follow-up scan', notes: 'Slight artifact on edge', exception: '' },

  // EQ-0007 (Infusion Pump) logs prior to repair
  { id: 'UL-0008', deviceId: 'EQ-0007', start: '2025-09-08T10:00:00Z', end: '2025-09-08T10:20:00Z', purpose: 'Infusion setup', notes: '', exception: 'Flow alarm triggered' },
  { id: 'UL-0009', deviceId: 'EQ-0007', start: '2025-09-08T10:30:00Z', end: '2025-09-08T10:45:00Z', purpose: 'Retry calibration', notes: 'Temporary fix', exception: '' },
  { id: 'UL-0010', deviceId: 'EQ-0007', start: '2025-09-09T09:00:00Z', end: '2025-09-09T09:10:00Z', purpose: 'Pre-repair test', notes: 'High noise', exception: '' },
]

export const deptRepairTickets = [
  { id: 'DR-0001', type: 'Repair', description: 'Strange noise', status: 'Pending Review', deviceId: 'EQ-0007', createdAt: '2025-09-11T08:30:00Z' },
  { id: 'DR-0002', type: 'Maintenance', description: 'Quarterly inspection', status: 'In Repair', deviceId: 'EQ-0001', createdAt: '2025-09-09T12:10:00Z' },
]

export const deptRequests = [
  { id: 'REQ-0001', kind: 'New Device', content: 'Need one ultrasound for room 3', status: 'Pending' },
]

export function clone(value) { return JSON.parse(JSON.stringify(value)) }


