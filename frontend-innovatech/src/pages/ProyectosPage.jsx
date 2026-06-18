import { useState, useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getProyectos, createProyecto, updateProyecto, deleteProyecto } from '../services/api.js'
import { Btn, Badge, Card, Modal, Input, Textarea, Select, ProgressBar, Spinner, EmptyState } from '../atoms/index.jsx'
import { useNotif } from '../context/NotifContext.jsx'

const ESTADOS = [
  { value:'PENDIENTE', label:'Pendiente' },
  { value:'EN_PROGRESO', label:'En Progreso' },
  { value:'FINALIZADO', label:'Finalizado' },
]

const EMPTY_FORM = { nombre:'', descripcion:'', estado:'PENDIENTE', avance:0, responsable:'' }

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
    </div>
  )
}
