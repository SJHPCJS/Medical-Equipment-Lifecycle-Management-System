// Mock data for Equipment Manager area

export const deviceStatuses = ['Unassigned', 'In Use', 'Under Repair', 'Disabled', 'Pending Scrap']

export const devices = [
  { id: 'EQ-0001', type: 'MRI Scanner', status: 'In Use', departmentId: 'DPT-004', vendor: 'MedTech Co.' },
  { id: 'EQ-0002', type: 'X-Ray Machine', status: 'Under Repair', departmentId: 'DPT-004', vendor: 'HealthCorp' },
  { id: 'EQ-0003', type: 'Ultrasound', status: 'Unassigned', departmentId: '', vendor: 'SonoPlus' },
  { id: 'EQ-0004', type: 'ECG Monitor', status: 'In Use', departmentId: 'DPT-001', vendor: 'Pulse Ltd.' },
  { id: 'EQ-0005', type: 'Ventilator', status: 'Pending Scrap', departmentId: 'DPT-006', vendor: 'AirLife' },
  { id: 'EQ-0006', type: 'Infusion Pump', status: 'Disabled', departmentId: 'DPT-002', vendor: 'CareFlow' },
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
  { id: 'S-0001', deviceId: 'EQ-0005', reason: 'Beyond economic repair', status: 'Pending', record: '' },
  { id: 'S-0002', deviceId: 'EQ-0012', reason: 'Obsolete', status: 'Pending', record: '' },
]

export function clone(value) { return JSON.parse(JSON.stringify(value)) }


