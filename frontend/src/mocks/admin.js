// Mock data for System Administrator area (frontend-only)
// All texts are English to match UI requirements

// Departments
export const departments = [
  { id: 'DPT-001', name: 'Cardiology' },
  { id: 'DPT-002', name: 'Neurology' },
  { id: 'DPT-003', name: 'Oncology' },
  { id: 'DPT-004', name: 'Radiology' },
  { id: 'DPT-005', name: 'Orthopedics' },
  { id: 'DPT-006', name: 'Emergency' },
  { id: 'DPT-007', name: 'Pediatrics' },
]

// User Types (4 types only)
export const roles = [
  { id: 'SYS_ADMIN', name: 'System Administrator' },
  { id: 'EQUIP_MANAGER', name: 'Equipment Manager' },
  { id: 'DEPT_USER', name: 'Department User' },
  { id: 'PROC_STAFF', name: 'Procurement Staff' },
]

// Users (staff)
// password is present only for edit/create flows; not shown in listing
export const users = [
  { id: 1, username: 'alice', roleId: 'SYS_ADMIN', departmentId: 'DPT-001', password: 'changeme' },
  { id: 2, username: 'bob', roleId: 'EQUIP_MANAGER', departmentId: 'DPT-002', password: 'changeme' },
  { id: 3, username: 'charlie', roleId: 'DEPT_USER', departmentId: 'DPT-006', password: 'changeme' },
  { id: 4, username: 'david', roleId: 'DEPT_USER', departmentId: 'DPT-004', password: 'changeme' },
  { id: 5, username: 'eve', roleId: 'PROC_STAFF', departmentId: 'DPT-003', password: 'changeme' },
  { id: 6, username: 'frank', roleId: 'DEPT_USER', departmentId: 'DPT-005', password: 'changeme' },
  { id: 7, username: 'grace', roleId: 'DEPT_USER', departmentId: 'DPT-007', password: 'changeme' },
  { id: 8, username: 'henry', roleId: 'DEPT_USER', departmentId: 'DPT-001', password: 'changeme' },
  { id: 9, username: 'irene', roleId: 'EQUIP_MANAGER', departmentId: 'DPT-002', password: 'changeme' },
  { id: 10, username: 'jack', roleId: 'DEPT_USER', departmentId: 'DPT-003', password: 'changeme' },
  { id: 11, username: 'kate', roleId: 'DEPT_USER', departmentId: 'DPT-004', password: 'changeme' },
  { id: 12, username: 'leo', roleId: 'DEPT_USER', departmentId: 'DPT-006', password: 'changeme' },
  { id: 13, username: 'mike', roleId: 'DEPT_USER', departmentId: 'DPT-005', password: 'changeme' },
  { id: 14, username: 'nina', roleId: 'DEPT_USER', departmentId: 'DPT-007', password: 'changeme' },
  { id: 15, username: 'oliver', roleId: 'DEPT_USER', departmentId: 'DPT-001', password: 'changeme' },
  { id: 16, username: 'peggy', roleId: 'DEPT_USER', departmentId: 'DPT-002', password: 'changeme' },
  { id: 17, username: 'quentin', roleId: 'DEPT_USER', departmentId: 'DPT-003', password: 'changeme' },
  { id: 18, username: 'rachel', roleId: 'DEPT_USER', departmentId: 'DPT-004', password: 'changeme' },
  { id: 19, username: 'steve', roleId: 'DEPT_USER', departmentId: 'DPT-005', password: 'changeme' },
  { id: 20, username: 'tina', roleId: 'DEPT_USER', departmentId: 'DPT-006', password: 'changeme' },
]

// Tickets summary for dashboard
export const ticketsSummary = {
  totalTickets: 523,
}

// Logs
export const logs = [
  { id: 'L-0001', timestamp: '2025-08-01T09:12:33Z', user: 'alice', target: 'System', action: 'Enable Maintenance Mode' },
  { id: 'L-0002', timestamp: '2025-08-02T10:22:01Z', user: 'bob', target: 'User: charlie', action: 'Reset Password' },
  { id: 'L-0003', timestamp: '2025-08-03T13:45:27Z', user: 'alice', target: 'Department: Radiology', action: 'Create' },
  { id: 'L-0004', timestamp: '2025-08-04T08:09:52Z', user: 'grace', target: 'Ticket: #412', action: 'Update Status' },
  { id: 'L-0005', timestamp: '2025-08-05T16:30:10Z', user: 'eve', target: 'User: mike', action: 'Assign Role' },
]

// Helper to get next user id (auto-increment)
export function getNextUserId(currentUsers) {
  const maxId = currentUsers.reduce((m, u) => Math.max(m, Number(u.id)), 0)
  return maxId + 1
}

// Utility to clone deep enough for our usage
export function clone(value) {
  return JSON.parse(JSON.stringify(value))
}


