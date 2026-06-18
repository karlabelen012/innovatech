import { useState, useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getEmpleados, createEmpleado, updateEmpleado, deleteEmpleado, updateDisponibilidad, getAsignaciones, createAsignacion, deleteAsignacion, getProyectos } from '../services/api.js'
import { Btn, Badge, Card, Modal, Input, Select, Spinner, EmptyState, Avatar } from '../atoms/index.jsx'
import { useNotif } from '../context/NotifContext.jsx'

const DISPONIBILIDADES = [
  { value:'DISPONIBLE', label:'Disponible' },
  { value:'OCUPADO', label:'Ocupado' },
  { value:'VACACIONES', label:'Vacaciones' },
  { value:'LICENCIA', label:'Licencia' },
]

const EMPTY_EMP = { nombre:'', apellido:'', email:'', telefono:'', rol:'', disponibilidad:'DISPONIBLE', horasSemanales:40, habilidades:'' }

export default function RecursosPage() {
  const { refreshTick } = useOutletContext()
  const { push } = useNotif()
  const [empleados, setEmpleados] = useState([])
  const [asignaciones, setAsignaciones] = useState([])
  const [proyectos, setProyectos] = useState([])
  const [loading, setLoading] = useState(true)
  const [tab, setTab] = useState('empleados')
  const [modal, setModal] = useState(null)
  const [selected, setSelected] = useState(null)
  const [form, setForm] = useState(EMPTY_EMP)
  const [asignForm, setAsignForm] = useState({ empleadoId:'', proyectoId:'' })
  const [saving, setSaving] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const [emps, asigs, projs] = await Promise.allSettled([getEmpleados(), getAsignaciones(), getProyectos()])
      setEmpleados(emps.value || [])
      setAsignaciones(asigs.value || [])
      setProyectos(projs.value || [])
    } catch { push('Error cargando recursos', 'error') }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [refreshTick])

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.type==='number' ? Number(e.target.value) : e.target.value }))

  const handleSave = async () => {
    if (!form.nombre.trim() || !form.apellido.trim() || !form.email.trim() || !form.rol.trim()) {
      push('Nombre, apellido, email y rol son obligatorios', 'warning'); return
    }
    setSaving(true)
    try {
      if (modal === 'create') { await createEmpleado(form); push('Empleado creado', 'success') }
      else { await updateEmpleado(selected.id, form); push('Empleado actualizado', 'success') }
      setModal(null); load()
    } catch (e) { push(e?.response?.data?.message || 'Error al guardar', 'error') }
    finally { setSaving(false) }
  }

  const handleDelete = async () => {
    setSaving(true)
    try { await deleteEmpleado(selected.id); push('Empleado dado de baja', 'success'); setModal(null); load() }
    catch { push('Error al eliminar', 'error') }
    finally { setSaving(false) }
  }

  const handleDisponibilidad = async (id, disp) => {
    try { await updateDisponibilidad(id, disp); push('Disponibilidad actualizada', 'success'); load() }
    catch { push('Error actualizando disponibilidad', 'error') }
  }

  const handleAsignacion = async () => {
    if (!asignForm.empleadoId || !asignForm.proyectoId) { push('Selecciona empleado y proyecto', 'warning'); return }
    setSaving(true)
    try {
      await createAsignacion({ empleadoId: Number(asignForm.empleadoId), proyectoId: Number(asignForm.proyectoId) })
      push('Asignación creada', 'success'); setModal(null); load()
    } catch (e) { push(e?.response?.data?.message || 'Error al crear asignación', 'error') }
    finally { setSaving(false) }
  }

  const handleDeleteAsig = async (id) => {
    try { await deleteAsignacion(id); push('Asignación desactivada', 'success'); load() }
    catch { push('Error', 'error') }
  }

  const getInitials = (nombre, apellido) => `${nombre?.[0]||''}${apellido?.[0]||''}`.toUpperCase()

  return (
    <div className="animate-fade-in">
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
        <div>
          <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22 }}>Gestión de Recursos</h2>
          <p style={{ color:'var(--text-muted)', fontSize:12, marginTop:2 }}>Empleados, disponibilidad y asignaciones</p>
        </div>
        <div style={{ display:'flex', gap:8 }}>
          {tab==='empleados' && <Btn onClick={()=>{ setForm(EMPTY_EMP); setSelected(null); setModal('create') }}>+ Nuevo Empleado</Btn>}
          {tab==='asignaciones' && <Btn onClick={()=>{ setAsignForm({empleadoId:'',proyectoId:''}); setModal('asignar') }}>+ Nueva Asignación</Btn>}
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display:'flex', gap:4, marginBottom:20, background:'var(--bg-card)', padding:4, borderRadius:'var(--radius-md)', width:'fit-content', border:'1px solid var(--border)' }}>
        {['empleados','asignaciones'].map(t => (
          <button key={t} onClick={()=>setTab(t)} style={{
            padding:'7px 20px', borderRadius:8, border:'none', cursor:'pointer',
            fontFamily:'var(--font-body)', fontSize:13, fontWeight:tab===t?600:400,
            background:tab===t?'var(--teal-dim)':'transparent',
            color:tab===t?'var(--teal)':'var(--text-secondary)',
            borderColor:tab===t?'var(--border-bright)':'transparent',
          }}>{t==='empleados'?'👥 Empleados':'🔗 Asignaciones'}</button>
        ))}
      </div>

      {loading ? <div style={{textAlign:'center',padding:40}}><Spinner size={28}/></div> : (
        <>
          {tab==='empleados' && (
            <Card style={{ padding:0, overflow:'hidden' }}>
              {empleados.length === 0 ? <EmptyState icon="👥" title="Sin empleados" description="Agrega el primer empleado." /> : (
                <table style={{ width:'100%', borderCollapse:'collapse' }}>
                  <thead style={{ background:'var(--bg-deep)' }}>
                    <tr>{['','Empleado','Rol','Email','Horas/sem','Disponibilidad','Acciones'].map(h=>(
                      <th key={h} style={{ textAlign:'left', padding:'12px 16px', fontSize:11, color:'var(--text-muted)', fontWeight:600 }}>{h}</th>
                    ))}</tr>
                  </thead>
                  <tbody>
                    {empleados.map(e => (
                      <tr key={e.id} style={{ borderTop:'1px solid var(--border)' }}
                        onMouseEnter={x=>x.currentTarget.style.background='var(--bg-card-hover)'}
                        onMouseLeave={x=>x.currentTarget.style.background='none'}>
                        <td style={{ padding:'12px 16px' }}>
                          <Avatar initials={getInitials(e.nombre, e.apellido)} size={34} />
                        </td>
                        <td style={{ padding:'12px 16px' }}>
                          <div style={{ fontSize:13, fontWeight:600 }}>{e.nombre} {e.apellido}</div>
                          <div style={{ fontSize:11, color:'var(--text-muted)' }}>{e.habilidades || '—'}</div>
                        </td>
                        <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)' }}>{e.rol}</td>
                        <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)' }}>{e.email}</td>
                        <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)' }}>{e.horasSemanales}h</td>
                        <td style={{ padding:'12px 16px' }}>
                          <select value={e.disponibilidad || 'DISPONIBLE'}
                            onChange={ev=>handleDisponibilidad(e.id, ev.target.value)}
                            style={{ background:'var(--bg-void)', border:'1px solid var(--border)', borderRadius:6, padding:'5px 8px', color:'var(--text-primary)', fontSize:11, cursor:'pointer' }}>
                            {DISPONIBILIDADES.map(d=><option key={d.value} value={d.value}>{d.label}</option>)}
                          </select>
                        </td>
                        <td style={{ padding:'12px 16px' }}>
                          <div style={{ display:'flex', gap:6 }}>
                            <Btn variant="icon" onClick={()=>{ setForm({ nombre:e.nombre||'', apellido:e.apellido||'', email:e.email||'', telefono:e.telefono||'', rol:e.rol||'', disponibilidad:e.disponibilidad||'DISPONIBLE', horasSemanales:e.horasSemanales||40, habilidades:e.habilidades||'' }); setSelected(e); setModal('edit') }} title="Editar">✏️</Btn>
                            <Btn variant="danger" style={{padding:'6px 8px'}} onClick={()=>{ setSelected(e); setModal('delete') }} title="Eliminar">🗑</Btn>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </Card>
          )}

          {tab==='asignaciones' && (
            <Card style={{ padding:0, overflow:'hidden' }}>
              {asignaciones.length === 0 ? <EmptyState icon="🔗" title="Sin asignaciones" description="Asigna empleados a proyectos." /> : (
                <table style={{ width:'100%', borderCollapse:'collapse' }}>
                  <thead style={{ background:'var(--bg-deep)' }}>
                    <tr>{['ID','Empleado ID','Proyecto ID','Estado','Acciones'].map(h=>(
                      <th key={h} style={{ textAlign:'left', padding:'12px 16px', fontSize:11, color:'var(--text-muted)', fontWeight:600 }}>{h}</th>
                    ))}</tr>
                  </thead>
                  <tbody>
                    {asignaciones.map(a => (
                      <tr key={a.id} style={{ borderTop:'1px solid var(--border)' }}
                        onMouseEnter={x=>x.currentTarget.style.background='var(--bg-card-hover)'}
                        onMouseLeave={x=>x.currentTarget.style.background='none'}>
                        <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-muted)' }}>#{a.id}</td>
                        <td style={{ padding:'12px 16px', fontSize:13 }}>Empleado #{a.empleadoId}</td>
                        <td style={{ padding:'12px 16px', fontSize:13 }}>Proyecto #{a.proyectoId}</td>
                        <td style={{ padding:'12px 16px' }}><Badge label={a.activo ? 'ACTIVO' : 'INACTIVO'} /></td>
                        <td style={{ padding:'12px 16px' }}>
                          <Btn variant="danger" style={{padding:'6px 8px'}} onClick={()=>handleDeleteAsig(a.id)} title="Desactivar">🗑</Btn>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </Card>
          )}
        </>
      )}

      {/* Employee Modal */}
      <Modal open={modal==='create'||modal==='edit'} onClose={()=>setModal(null)} title={modal==='create'?'+ Nuevo Empleado':'Editar Empleado'}>
        <div style={{ display:'flex', flexDirection:'column', gap:12 }}>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <Input label="Nombre" name="nombre" value={form.nombre} onChange={handleChange} required />
            <Input label="Apellido" name="apellido" value={form.apellido} onChange={handleChange} required />
          </div>
          <Input label="Email" name="email" type="email" value={form.email} onChange={handleChange} required />
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <Input label="Teléfono" name="telefono" value={form.telefono} onChange={handleChange} />
            <Input label="Rol" name="rol" value={form.rol} onChange={handleChange} required placeholder="Ej: Desarrollador" />
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <Select label="Disponibilidad" name="disponibilidad" value={form.disponibilidad} onChange={handleChange} options={DISPONIBILIDADES} />
            <Input label="Horas semanales" name="horasSemanales" type="number" value={form.horasSemanales} onChange={handleChange} min={1} max={80} />
          </div>
          <Input label="Habilidades" name="habilidades" value={form.habilidades} onChange={handleChange} placeholder="React, Spring Boot..." />
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end', marginTop:8 }}>
            <Btn variant="secondary" onClick={()=>setModal(null)}>Cancelar</Btn>
            <Btn onClick={handleSave} disabled={saving}>{saving?<Spinner size={14} color="#0a0d14"/>:'Guardar'}</Btn>
          </div>
        </div>
      </Modal>

      {/* Delete confirm */}
      <Modal open={modal==='delete'} onClose={()=>setModal(null)} title="Dar de baja empleado" width={400}>
        <p style={{ color:'var(--text-secondary)', fontSize:14, marginBottom:20 }}>
          ¿Confirmas dar de baja a <strong style={{color:'var(--text-primary)'}}>{selected?.nombre} {selected?.apellido}</strong>?
        </p>
        <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
          <Btn variant="secondary" onClick={()=>setModal(null)}>Cancelar</Btn>
          <Btn variant="danger" onClick={handleDelete} disabled={saving}>{saving?<Spinner size={14}/>:'Confirmar'}</Btn>
        </div>
      </Modal>

      {/* Asignación modal */}
      <Modal open={modal==='asignar'} onClose={()=>setModal(null)} title="+ Nueva Asignación" width={420}>
        <div style={{ display:'flex', flexDirection:'column', gap:12 }}>
          <Select label="Empleado" name="empleadoId" value={asignForm.empleadoId}
            onChange={e=>setAsignForm(f=>({...f,empleadoId:e.target.value}))} required
            options={empleados.map(e=>({ value:e.id, label:`${e.nombre} ${e.apellido} (${e.rol})` }))} />
          <Select label="Proyecto" name="proyectoId" value={asignForm.proyectoId}
            onChange={e=>setAsignForm(f=>({...f,proyectoId:e.target.value}))} required
            options={proyectos.map(p=>({ value:p.id, label:`${p.nombre} (${p.estado})` }))} />
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end', marginTop:8 }}>
            <Btn variant="secondary" onClick={()=>setModal(null)}>Cancelar</Btn>
            <Btn onClick={handleAsignacion} disabled={saving}>{saving?<Spinner size={14} color="#0a0d14"/>:'Asignar'}</Btn>
          </div>
        </div>
      </Modal>
    </div>
  )
}
