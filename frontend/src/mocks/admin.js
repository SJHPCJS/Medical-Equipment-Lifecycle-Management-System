// Mock data for System Administrator area (frontend-only)
// All texts are English to match UI requirements

// Departments
export const departments = []

// User Types (4 types only)
export const roles = [
  { id: 'SYS_ADMIN', name: 'System Administrator' },
  { id: 'EQUIP_MANAGER', name: 'Equipment Manager' },
  { id: 'DEPT_USER', name: 'Department User' },
  { id: 'PROC_STAFF', name: 'Procurement Staff' },
]

// Users (staff)
// password is present only for edit/create flows; not shown in listing
export const users = []

// Tickets summary for dashboard
export const ticketsSummary = { totalTickets: 0 }

// Logs
export const logs = []

// Helper to get next user id (auto-increment)
export function getNextUserId(currentUsers) {
  const maxId = currentUsers.reduce((m, u) => Math.max(m, Number(u.id)), 0)
  return maxId + 1
}

// Utility to clone deep enough for our usage
export function clone(value) {
  return JSON.parse(JSON.stringify(value))
}


