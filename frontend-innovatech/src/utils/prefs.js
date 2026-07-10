// Preferencias del usuario persistidas en localStorage.
// Centralizadas aquí para que Configuración, Dashboard y KPIs lean/escriban
// siempre las mismas claves con el mismo formato.

export const POLL_INTERVAL_KEY = 'innovatech_poll_interval'
export const DEFAULT_POLL_INTERVAL = 15000

export function getPollInterval() {
  try {
    const v = Number(localStorage.getItem(POLL_INTERVAL_KEY))
    return Number.isFinite(v) && v > 0 ? v : DEFAULT_POLL_INTERVAL
  } catch {
    return DEFAULT_POLL_INTERVAL
  }
}

export function setPollInterval(ms) {
  try { localStorage.setItem(POLL_INTERVAL_KEY, String(ms)) } catch { /* noop */ }
}
