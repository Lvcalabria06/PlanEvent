import './estoque.css'
import { useEventosFinanceiro } from '../financeiro/hooks/useEventosFinanceiro'
import { EstoqueProvider } from './EstoqueContext'
import EstoqueModule from './EstoqueModule'

export default function EstoqueApp() {
  const { eventos, carregando, erro } = useEventosFinanceiro()

  if (carregando) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        Carregando estoque…
      </div>
    )
  }

  if (erro) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        <p style={{ margin: 0 }}>
          Não foi possível carregar os eventos. Verifique se o backend está em execução na porta 3000.
        </p>
      </div>
    )
  }

  if (eventos.length === 0) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '3rem', color: '#6b7280' }}>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#374151', margin: '0 0 0.5rem 0' }}>
          Nenhum evento cadastrado
        </h3>
        <p style={{ margin: 0, fontSize: '0.9rem' }}>
          Crie um evento na seção <strong>Eventos</strong> para utilizar o estoque.
        </p>
      </div>
    )
  }

  const eventosRef = eventos.map((e) => ({ id: e.id, nome: e.nome }))

  return (
    <EstoqueProvider eventos={eventosRef}>
      <div className="estoque-root">
        <EstoqueModule />
      </div>
    </EstoqueProvider>
  )
}
