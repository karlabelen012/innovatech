import { useState, useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  getProyectos, createProyecto, updateProyecto, deleteProyecto,
  getTareasPorProyecto, createTarea, updateTarea, deleteTarea,
} from '../services/api.js'
import { Btn, Badge, Card, Modal, Input, Textarea, Select, ProgressBar, Spinner, EmptyState } from '../atoms/index.jsx'
import { useNotif } from '../context/NotifContext.jsx'

const ESTADOS = [
  { value:'PENDIENTE', label:'Pendiente' },
  { value:'EN_PROGRESO', label:'En Progreso' },
  { value:'FINALIZADO', label:'Finalizado' },
]

const EMPTY_FORM = { nombre:'', descripcion:'', estado:'PENDIENTE', avance:0, responsable:'' }
const EMPTY_TAREA = { titulo:'', descripcion:'', estado:'PENDIENTE', responsable:'' }

export default function ProyectosPage() {
  const { refreshTick } = useOutletContext()
  const { push } = useNotif()
  const [proyectos, setProyectos] = useState([])
  const [loading, setLoading] = useState(true)
  const [filterEstado, setFilterEstado] = useState('')
  const [modal, setModal] = useState(null) // null | 'create' | 'edit' | 'delete'
  const [selected, setSelected] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [saving, setSaving] = useState(false)

  // ── Tareas ──────────────────────────────────────────────
  const [tareasProyecto, setTareasProyecto] = useState(null) // proyecto seleccionado para ver tareas
  const [tareas, setTareas] = useState([])
  const [tareasLoading, setTareasLoading] = useState(false)
  const [tareaModal, setTareaModal] = useState(null) // null | 'create' | 'edit' | 'delete'
  const [tareaSeleccionada, setTareaSeleccionada] = useState(null)
  const [tareaForm, setTareaForm] = useState(EMPTY_TAREA)
  const [tareaSaving, setTareaSaving] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const data = await getProyectos(filterEstado || undefined)
      setProyectos(data)
    } catch {
      push('No se pudo cargar proyectos. Verifica el BFF.', 'error')
      setProyectos([])
    } finally { setLoading(false) }
  }

  useEffect(() => { load() }, [refreshTick, filterEstado])

  const openCreate = () => { setForm(EMPTY_FORM); setSelected(null); setModal('create') }
  const openEdit = (p) => { setForm({ nombre:p.nombre||'', descripcion:p.descripcion||'', estado:p.estado||'PENDIENTE', avance:p.avance||0, responsable:p.responsable||'' }); setSelected(p); setModal('edit') }
  const openDelete = (p) => { setSelected(p); setModal('delete') }
  const closeModal = () => { setModal(null); setSelected(null) }

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.type === 'number' ? Number(e.target.value) : e.target.value }))

  const handleSave = async () => {
    if (!form.nombre.trim()) { push('El nombre es obligatorio', 'warning'); return }
    setSaving(true)
    try {
      if (modal === 'create') {
        await createProyecto(form)
        push('Proyecto creado correctamente', 'success')
      } else {
        await updateProyecto(selected.id, form)
        push('Proyecto actualizado', 'success')
      }
      closeModal(); load()
    } catch (e) {
      push(e?.response?.data?.message || 'Error al guardar proyecto', 'error')
    } finally { setSaving(false) }
  }

  const handleDelete = async () => {
    setSaving(true)
    try {
      await deleteProyecto(selected.id)
      push('Proyecto eliminado', 'success')
      closeModal(); load()
    } catch { push('Error al eliminar', 'error') }
    finally { setSaving(false) }
  }

  // ── Tareas: handlers ──────────────────────────────────────
  const loadTareas = async (proyectoId) => {
    setTareasLoading(true)
    try {
      const data = await getTareasPorProyecto(proyectoId)
      setTareas(data)
    } catch {
      push('No se pudieron cargar las tareas del proyecto', 'error')
      setTareas([])
    } finally { setTareasLoading(false) }
  }

  const openTareas = (p) => { setTareasProyecto(p); loadTareas(p.id) }
  const closeTareas = () => { setTareasProyecto(null); setTareas([]); setTareaModal(null); setTareaSeleccionada(null) }

  const openTareaCreate = () => { setTareaForm(EMPTY_TAREA); setTareaSeleccionada(null); setTareaModal('create') }
  const openTareaEdit = (t) => { setTareaForm({ titulo:t.titulo||'', descripcion:t.descripcion||'', estado:t.estado||'PENDIENTE', responsable:t.responsable||'' }); setTareaSeleccionada(t); setTareaModal('edit') }
  const openTareaDelete = (t) => { setTareaSeleccionada(t); setTareaModal('delete') }
  const closeTareaModal = () => { setTareaModal(null); setTareaSeleccionada(null) }

  const handleTareaChange = e => setTareaForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const handleTareaSave = async () => {
    if (!tareaForm.titulo.trim()) { push('El título es obligatorio', 'warning'); return }
    setTareaSaving(true)
    try {
      if (tareaModal === 'create') {
        await createTarea({ ...tareaForm, proyectoId: tareasProyecto.id })
        push('Tarea creada correctamente', 'success')
      } else {
        await updateTarea(tareaSeleccionada.id, { ...tareaForm, proyectoId: tareasProyecto.id })
        push('Tarea actualizada', 'success')
      }
      closeTareaModal(); loadTareas(tareasProyecto.id)
    } catch (e) {
      push(e?.response?.data?.message || 'Error al guardar la tarea', 'error')
    } finally { setTareaSaving(false) }
  }

  const handleTareaDelete = async () => {
    setTareaSaving(true)
    try {
      await deleteTarea(tareaSeleccionada.id)
      push('Tarea eliminada', 'success')
      closeTareaModal(); loadTareas(tareasProyecto.id)
    } catch { push('Error al eliminar la tarea', 'error') }
    finally { setTareaSaving(false) }
  }

  return (
    <div className="animate-fade-in">
      {/* Header */}
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
        <div>
          <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22 }}>Gestión de Proyectos</h2>
          <p style={{ color:'var(--text-muted)', fontSize:12, marginTop:2 }}>Planifica, asigna y controla el avance de los proyectos</p>
        </div>
        <div style={{ display:'flex', gap:10, alignItems:'center' }}>
          <select value={filterEstado} onChange={e=>setFilterEstado(e.target.value)}
            style={{ background:'var(--bg-card)', border:'1px solid var(--border)', borderRadius:'var(--radius-sm)', padding:'8px 12px', color:'var(--text-primary)', fontSize:12, cursor:'pointer' }}>
            <option value="">Todos los estados</option>
            {ESTADOS.map(e => <option key={e.value} value={e.value}>{e.label}</option>)}
          </select>
          <Btn onClick={openCreate}>+ Nuevo Proyecto</Btn>
        </div>
      </div>

      {/* Table */}
      <Card style={{ padding:0, overflow:'hidden' }}>
        {loading ? (
          <div style={{ padding:40, textAlign:'center' }}><Spinner size={28} /></div>
        ) : proyectos.length === 0 ? (
          <EmptyState icon="📋" title="Sin proyectos" description="Crea tu primer proyecto con el botón de arriba." />
        ) : (
          <table style={{ width:'100%', borderCollapse:'collapse' }}>
            <thead style={{ background:'var(--bg-deep)' }}>
              <tr>{['ID','Proyecto','Descripción','Responsable','Estado','Avance','Acciones'].map(h => (
                <th key={h} style={{ textAlign:'left', padding:'12px 16px', fontSize:11, color:'var(--text-muted)', fontWeight:600, letterSpacing:'0.05em' }}>{h}</th>
              ))}</tr>
            </thead>
            <tbody>
              {proyectos.map(p => (
                <tr key={p.id} style={{ borderTop:'1px solid var(--border)', transition:'var(--transition)' }}
                  onMouseEnter={e=>e.currentTarget.style.background='var(--bg-card-hover)'}
                  onMouseLeave={e=>e.currentTarget.style.background='none'}>
                  <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-muted)' }}>#{p.id}</td>
                  <td style={{ padding:'12px 16px', fontSize:13, fontWeight:600, color:'var(--text-primary)' }}>{p.nombre}</td>
                  <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)', maxWidth:200, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>{p.descripcion || '—'}</td>
                  <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)' }}>{p.responsable || '—'}</td>
                  <td style={{ padding:'12px 16px' }}><Badge label={p.estado} /></td>
                  <td style={{ padding:'12px 16px', minWidth:140 }}>
                    <div style={{ display:'flex', alignItems:'center', gap:8 }}>
                      <ProgressBar value={p.avance || 0} />
                      <span style={{ fontSize:11, color:'var(--text-muted)', width:32 }}>{p.avance || 0}%</span>
                    </div>
                  </td>
                  <td style={{ padding:'12px 16px' }}>
                    <div style={{ display:'flex', gap:6 }}>
                      <Btn variant="icon" onClick={()=>openTareas(p)} title="Ver tareas">📝</Btn>
                      <Btn variant="icon" onClick={()=>openEdit(p)} title="Editar">✏️</Btn>
                      <Btn variant="danger" style={{padding:'6px 8px'}} onClick={()=>openDelete(p)} title="Eliminar">🗑</Btn>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>

      {/* Create / Edit Modal */}
      <Modal open={modal==='create'||modal==='edit'} onClose={closeModal}
        title={modal==='create' ? '+ Nuevo Proyecto' : 'Editar Proyecto'}>
        <div style={{ display:'flex', flexDirection:'column', gap:14 }}>
          <Input label="Nombre del proyecto" name="nombre" value={form.nombre} onChange={handleChange} required placeholder="Ej: Portal Fintech" />
          <Textarea label="Descripción" name="descripcion" value={form.descripcion} onChange={handleChange} placeholder="Descripción del proyecto..." rows={3} />
          <Select label="Estado" name="estado" value={form.estado} onChange={handleChange} options={ESTADOS} required />
          <Input label="Avance (%)" name="avance" type="number" value={form.avance} onChange={handleChange} min={0} max={100} />
          <Input label="Responsable" name="responsable" value={form.responsable} onChange={handleChange} placeholder="Nombre del responsable" />
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end', marginTop:8 }}>
            <Btn variant="secondary" onClick={closeModal}>Cancelar</Btn>
            <Btn onClick={handleSave} disabled={saving}>{saving ? <Spinner size={14} color="#0a0d14" /> : 'Guardar'}</Btn>
          </div>
        </div>
      </Modal>

      {/* Delete confirm */}
      <Modal open={modal==='delete'} onClose={closeModal} title="Eliminar Proyecto" width={400}>
        <p style={{ color:'var(--text-secondary)', fontSize:14, marginBottom:20 }}>
          ¿Confirmas eliminar el proyecto <strong style={{ color:'var(--text-primary)' }}>"{selected?.nombre}"</strong>? Esta acción no se puede deshacer.
        </p>
        <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
          <Btn variant="secondary" onClick={closeModal}>Cancelar</Btn>
          <Btn variant="danger" onClick={handleDelete} disabled={saving}>{saving ? <Spinner size={14}/> : 'Eliminar'}</Btn>
        </div>
      </Modal>

      {/* Tareas del proyecto */}
      <Modal open={!!tareasProyecto} onClose={closeTareas} title={`Tareas — ${tareasProyecto?.nombre || ''}`} width={640}>
        <div style={{ display:'flex', justifyContent:'flex-end', marginBottom:14 }}>
          <Btn onClick={openTareaCreate}>+ Nueva Tarea</Btn>
        </div>
        {tareasLoading ? (
          <div style={{ textAlign:'center', padding:30 }}><Spinner size={24} /></div>
        ) : tareas.length === 0 ? (
          <EmptyState icon="📝" title="Sin tareas" description="Agrega la primera tarea de este proyecto." />
        ) : (
          <div style={{ display:'flex', flexDirection:'column', gap:8, maxHeight:360, overflowY:'auto' }}>
            {tareas.map(t => (
              <div key={t.id} style={{
                display:'flex', justifyContent:'space-between', alignItems:'center', gap:10,
                padding:'10px 14px', background:'var(--bg-elevated)', borderRadius:'var(--radius-sm)',
                border:'1px solid var(--border)',
              }}>
                <div style={{ minWidth:0 }}>
                  <div style={{ fontSize:13, fontWeight:600, color:'var(--text-primary)' }}>{t.titulo}</div>
                  <div style={{ fontSize:11, color:'var(--text-muted)', marginTop:2 }}>{t.responsable || 'Sin responsable'}</div>
                </div>
                <div style={{ display:'flex', alignItems:'center', gap:8, flexShrink:0 }}>
                  <Badge label={t.estado} />
                  <Btn variant="icon" onClick={()=>openTareaEdit(t)} title="Editar">✏️</Btn>
                  <Btn variant="danger" style={{padding:'6px 8px'}} onClick={()=>openTareaDelete(t)} title="Eliminar">🗑</Btn>
                </div>
              </div>
            ))}
          </div>
        )}
      </Modal>

      {/* Crear/Editar tarea */}
      <Modal open={tareaModal==='create'||tareaModal==='edit'} onClose={closeTareaModal}
        title={tareaModal==='create' ? '+ Nueva Tarea' : 'Editar Tarea'}>
        <div style={{ display:'flex', flexDirection:'column', gap:14 }}>
          <Input label="Título" name="titulo" value={tareaForm.titulo} onChange={handleTareaChange} required placeholder="Ej: Diseñar base de datos" />
          <Textarea label="Descripción" name="descripcion" value={tareaForm.descripcion} onChange={handleTareaChange} placeholder="Descripción de la tarea..." rows={3} />
          <Select label="Estado" name="estado" value={tareaForm.estado} onChange={handleTareaChange} options={ESTADOS} required />
          <Input label="Responsable" name="responsable" value={tareaForm.responsable} onChange={handleTareaChange} placeholder="Nombre del responsable" />
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end', marginTop:8 }}>
            <Btn variant="secondary" onClick={closeTareaModal}>Cancelar</Btn>
            <Btn onClick={handleTareaSave} disabled={tareaSaving}>{tareaSaving ? <Spinner size={14} color="#0a0d14" /> : 'Guardar'}</Btn>
          </div>
        </div>
      </Modal>

      {/* Eliminar tarea */}
      <Modal open={tareaModal==='delete'} onClose={closeTareaModal} title="Eliminar Tarea" width={400}>
        <p style={{ color:'var(--text-secondary)', fontSize:14, marginBottom:20 }}>
          ¿Confirmas eliminar la tarea <strong style={{ color:'var(--text-primary)' }}>"{tareaSeleccionada?.titulo}"</strong>? Esta acción no se puede deshacer.
        </p>
        <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
          <Btn variant="secondary" onClick={closeTareaModal}>Cancelar</Btn>
          <Btn variant="danger" onClick={handleTareaDelete} disabled={tareaSaving}>{tareaSaving ? <Spinner size={14}/> : 'Eliminar'}</Btn>
        </div>
      </Modal>
    </div>
  )
}
