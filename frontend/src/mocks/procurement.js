// Mock data for Procurement Staff area

export const vendors = [
  { id: 'V-001', name: 'MedTech Co.', contact: 'Anna Li', address: '12 Health St, City', items: [
    { sku: 'MRI-01', name: 'MRI Scanner', price: 250000 },
    { sku: 'ECG-01', name: 'ECG Monitor', price: 1800 },
  ] },
  { id: 'V-002', name: 'HealthCorp', contact: 'John Smith', address: '88 Wellness Ave, City', items: [
    { sku: 'XRAY-01', name: 'X-Ray Machine', price: 60000 },
    { sku: 'VENT-01', name: 'Ventilator', price: 8000 },
  ] },
]

export const plans = [
  { id: 'PLAN-0001', name: 'ECG Monitor', spec: '12-lead', qty: 10, department: 'Cardiology', status: 'Pending' },
  { id: 'PLAN-0002', name: 'Ultrasound', spec: 'Portable', qty: 2, department: 'Emergency', status: 'Pending' },
]

export const orders = [
  { id: 'PO-0001', sku: 'ECG-01', itemName: 'ECG Monitor', qty: 10, vendorId: 'V-001', price: 1800, status: 'Confirmed Arrival' },
]

export const budgetUsage = {
  totalSpent: 250000 + 10*1800,
}

export function calcPrice(vendorId, sku, qty) {
  const v = vendors.find(x => x.id === vendorId)
  const item = v?.items?.find(i => i.sku === sku)
  return item ? item.price * qty : 0
}

export function clone(value) { return JSON.parse(JSON.stringify(value)) }



