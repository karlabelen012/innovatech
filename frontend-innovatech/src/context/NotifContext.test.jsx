import { describe, it, expect } from 'vitest'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { NotifProvider, useNotif } from './NotifContext.jsx'

function NotifConsumer() {
  const { toasts, push, remove } = useNotif()
  return (
    <div>
      <span data-testid="count">{toasts.length}</span>
      {toasts.map(t => (
        <div key={t.id} data-testid={`toast-${t.id}`}>
          <span>{t.msg}</span>
          <span data-testid={`type-${t.id}`}>{t.type}</span>
          <button onClick={() => remove(t.id)}>x</button>
        </div>
      ))}
      <button onClick={() => push('Operación exitosa', 'success')}>push-success</button>
      <button onClick={() => push('Error crítico', 'error')}>push-error</button>
      <button onClick={() => push('Info', 'info')}>push-info</button>
    </div>
  )
}

function renderNotif() {
  return render(
    <NotifProvider>
      <NotifConsumer />
    </NotifProvider>
  )
}

describe('NotifContext', () => {
  it('inicia sin toasts', () => {
    renderNotif()
    expect(screen.getByTestId('count').textContent).toBe('0')
  })

  it('push agrega un toast con el tipo correcto', () => {
    renderNotif()
    fireEvent.click(screen.getByText('push-success'))

    expect(screen.getByTestId('count').textContent).toBe('1')
    expect(screen.getByText('Operación exitosa')).toBeInTheDocument()
    expect(screen.getByTestId('type-1').textContent).toBe('success')
  })

  it('se pueden agregar múltiples toasts simultáneos', () => {
    renderNotif()
    fireEvent.click(screen.getByText('push-success'))
    fireEvent.click(screen.getByText('push-error'))
    fireEvent.click(screen.getByText('push-info'))

    expect(screen.getByTestId('count').textContent).toBe('3')
    expect(screen.getByText('Operación exitosa')).toBeInTheDocument()
    expect(screen.getByText('Error crítico')).toBeInTheDocument()
    expect(screen.getByText('Info')).toBeInTheDocument()
  })

  it('remove elimina el toast correspondiente', () => {
    renderNotif()
    fireEvent.click(screen.getByText('push-success'))
    fireEvent.click(screen.getByText('push-error'))

    expect(screen.getByTestId('count').textContent).toBe('2')

    const btns = screen.getAllByText('x')
    fireEvent.click(btns[0])

    expect(screen.getByTestId('count').textContent).toBe('1')
    expect(screen.queryByText('Operación exitosa')).not.toBeInTheDocument()
    expect(screen.getByText('Error crítico')).toBeInTheDocument()
  })

  it('el toast desaparece automáticamente tras la duración', async () => {
    renderNotif()
    fireEvent.click(screen.getByText('push-success'))

    expect(screen.getByTestId('count').textContent).toBe('1')

    await waitFor(
      () => expect(screen.getByTestId('count').textContent).toBe('0'),
      { timeout: 4000 }
    )
  }, 6000)
})
