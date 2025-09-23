// Utilities for device images

export const DEFAULT_DEVICE_PLACEHOLDER = `data:image/svg+xml;utf8,${encodeURIComponent(
  `<svg xmlns='http://www.w3.org/2000/svg' width='800' height='450' viewBox='0 0 800 450'>
    <defs>
      <linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>
        <stop offset='0%' stop-color='#f3f4f6'/>
        <stop offset='100%' stop-color='#e5e7eb'/>
      </linearGradient>
    </defs>
    <rect width='800' height='450' fill='url(#g)'/>
    <g fill='none' stroke='#9ca3af' stroke-width='2'>
      <rect x='180' y='90' width='440' height='270' rx='12'/>
      <circle cx='400' cy='225' r='60'/>
    </g>
    <text x='400' y='420' text-anchor='middle' font-family='Arial, Helvetica, sans-serif' font-size='20' fill='#6b7280'>No Image</text>
  </svg>`
)}`

export function getDeviceImageUrl(deviceId) {
  // Images are expected at /devices/<id>.jpg under public/
  return `/devices/${deviceId}.jpg`
}

export function onDeviceImgError(event) {
  if (event && event.target && event.target.src !== DEFAULT_DEVICE_PLACEHOLDER) {
    event.target.src = DEFAULT_DEVICE_PLACEHOLDER
  }
}


