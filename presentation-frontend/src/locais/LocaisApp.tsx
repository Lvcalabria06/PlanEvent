import { useState, useEffect, useCallback } from 'react';
import { toast } from 'sonner';
import {
  listarLocais,
  buscarLocal,
  cadastrarLocal,
  editarLocal,
  desativarLocal,
  listarManutencoes,
  cadastrarManutencao,
  editarManutencao,
  removerManutencao,
} from '../api/locaisApi';
import type { LocalDTO, ManutencaoDTO, ManutencaoCreateDTO, LocalCreateDTO } from '../api/locaisApi';
import './locais.css';

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

const INFRA_OPCOES = [
  'Wi-Fi',
  'Estacionamento',
  'Catering',
  'A/V Premium',
  'Climatização',
  'Acessibilidade',
  'Palco',
  'Vestiários',
  'Cozinha',
];

const TIPOS = ['AUDITÓRIO', 'SALÃO', 'ESPAÇO MULTIUSO', 'SALA DE REUNIÃO', 'ARENA', 'OUTRO'];

function formatCusto(val: number) {
  if (!val && val !== 0) return '—';
  if (val >= 1000) return `${Math.round(val / 1000)}K`;
  return String(val);
}

function formatCustoFull(val: number) {
  return `R$ ${Number(val).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
}

function formatDate(dateStr: string) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString('pt-BR') + ', ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
}

function formatDateShort(dateStr: string) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString('pt-BR');
}

function parseInfra(infraStr: string): string[] {
  if (!infraStr) return [];
  return infraStr.split(',').map((s) => s.trim()).filter(Boolean);
}

function joinInfra(infras: string[]): string {
  return infras.join(', ');
}

function isEmManutencao(local: LocalDTO): boolean {
  return local.status === 'EM_MANUTENCAO';
}

function genLocalId(): string {
  return 'VEN-' + String(Math.floor(Math.random() * 99999)).padStart(5, '0');
}

// Status display helpers
function StatusBadge({ status }: { status: string }) {
  if (status === 'ATIVO') {
    return (
      <span className="loc-status-badge loc-status-ativo">
        <span className="loc-status-dot" />
        Ativo
      </span>
    );
  }
  if (status === 'EM_MANUTENCAO') {
    return (
      <span className="loc-status-badge loc-status-manutencao">
        🔧 Em Manutenção
      </span>
    );
  }
  return (
    <span className="loc-status-badge loc-status-inativo">
      <span className="loc-status-dot loc-dot-gray" />
      Inativo
    </span>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Manutencao Modal
// ─────────────────────────────────────────────────────────────────────────────
interface ManutencaoModalProps {
  localId: string;
  manutencao?: ManutencaoDTO | null;
  onClose: () => void;
  onSaved: () => void;
}

function ManutencaoModal({ localId, manutencao, onClose, onSaved }: ManutencaoModalProps) {
  const isEdit = !!manutencao;
  const [dataInicio, setDataInicio] = useState(
    manutencao?.dataInicio ? manutencao.dataInicio.split('T')[0] : ''
  );
  const [dataFim, setDataFim] = useState(
    manutencao?.dataFim ? manutencao.dataFim.split('T')[0] : ''
  );
  const [responsavel, setResponsavel] = useState(manutencao?.responsavel ?? '');
  const [descricao, setDescricao] = useState(manutencao?.descricao ?? '');
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const e: Record<string, string> = {};
    if (!dataInicio) e.dataInicio = 'Data de início é obrigatória.';
    if (!dataFim) e.dataFim = 'Data de término é obrigatória.';
    if (dataInicio && dataFim && dataFim < dataInicio)
      e.dataFim = 'A data de término não pode ser anterior ao início.';
    if (!responsavel.trim()) e.responsavel = 'Responsável é obrigatório.';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);
    try {
      const dto: ManutencaoCreateDTO = {
        dataInicio: dataInicio + 'T00:00:00',
        dataFim: dataFim + 'T23:59:59',
        responsavel: responsavel.trim(),
        descricao: descricao.trim() || undefined,
      };
      if (isEdit) {
        await editarManutencao(localId, manutencao!.id, dto);
        toast.success('Manutenção atualizada com sucesso!');
      } else {
        await cadastrarManutencao(localId, dto);
        toast.success('Manutenção registrada com sucesso!');
      }
      onSaved();
      onClose();
    } catch (err: any) {
      toast.error(err?.message || 'Erro ao salvar manutenção.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="loc-modal-overlay" onClick={onClose}>
      <div className="loc-modal" onClick={(e) => e.stopPropagation()}>
        <div className="loc-modal-header">
          <div className="loc-modal-title-row">
            <span className="loc-wrench-icon">🔧</span>
            <h3>Registrar Manutenção</h3>
          </div>
          <button className="loc-modal-close" onClick={onClose}>✕</button>
        </div>

        <form onSubmit={handleSubmit} className="loc-modal-body">
          <div className="loc-form-row">
            <div className="loc-form-group">
              <label className="loc-label">
                Data de Início <span className="loc-required">*</span>
              </label>
              <input
                type="date"
                className={`loc-input ${errors.dataInicio ? 'loc-input-error' : ''}`}
                value={dataInicio}
                onChange={(e) => setDataInicio(e.target.value)}
              />
              {errors.dataInicio && <span className="loc-error-msg">{errors.dataInicio}</span>}
            </div>
            <div className="loc-form-group">
              <label className="loc-label">
                Data de Término <span className="loc-required">*</span>
              </label>
              <input
                type="date"
                className={`loc-input ${errors.dataFim ? 'loc-input-error' : ''}`}
                value={dataFim}
                onChange={(e) => setDataFim(e.target.value)}
              />
              {errors.dataFim && <span className="loc-error-msg">{errors.dataFim}</span>}
            </div>
          </div>

          <div className="loc-form-group">
            <label className="loc-label">
              Responsável <span className="loc-required">*</span>
            </label>
            <input
              type="text"
              className={`loc-input ${errors.responsavel ? 'loc-input-error' : ''}`}
              placeholder="Ex: Equipe Técnica TechFix"
              value={responsavel}
              onChange={(e) => setResponsavel(e.target.value)}
            />
            {errors.responsavel && <span className="loc-error-msg">{errors.responsavel}</span>}
          </div>

          <div className="loc-form-group">
            <label className="loc-label">Descrição / Observações</label>
            <textarea
              className="loc-textarea"
              placeholder="Descreva o tipo de manutenção a ser realizada..."
              value={descricao}
              onChange={(e) => setDescricao(e.target.value)}
              rows={3}
            />
          </div>

          <div className="loc-modal-info-box">
            <span className="loc-info-icon">ℹ</span>
            <p>
              Durante o período de manutenção, o local ficará{' '}
              <strong>indisponível para novos agendamentos</strong>.
            </p>
          </div>

          <div className="loc-modal-actions">
            <button type="button" className="loc-btn-outline" onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="loc-btn-primary" disabled={saving}>
              <span>💾</span>
              {saving ? 'Salvando...' : 'Registrar Manutenção'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Confirm Desativar Modal
// ─────────────────────────────────────────────────────────────────────────────
interface ConfirmDesativarProps {
  local: LocalDTO;
  onCancel: () => void;
  onConfirm: () => void;
}

function ConfirmDesativarModal({ local, onCancel, onConfirm }: ConfirmDesativarProps) {
  return (
    <div className="loc-modal-overlay" onClick={onCancel}>
      <div className="loc-modal loc-modal-sm" onClick={(e) => e.stopPropagation()}>
        <div className="loc-modal-header">
          <h3 style={{ color: '#ef4444' }}>Desativar Local</h3>
          <button className="loc-modal-close" onClick={onCancel}>✕</button>
        </div>
        <div className="loc-modal-body">
          <p style={{ marginBottom: '1.25rem', color: '#374151' }}>
            Tem certeza que deseja desativar o local <strong>"{local.nome}"</strong>? O local não
            estará mais disponível para novos agendamentos.
          </p>
          <div className="loc-modal-actions">
            <button className="loc-btn-outline" onClick={onCancel}>
              Cancelar
            </button>
            <button className="loc-btn-danger" onClick={onConfirm}>
              Desativar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Local Form (Create / Edit)
// ─────────────────────────────────────────────────────────────────────────────
interface LocalFormProps {
  local?: LocalDTO | null;
  onBack: () => void;
  onSaved: () => void;
}

function LocalForm({ local, onBack, onSaved }: LocalFormProps) {
  const isEdit = !!local;
  const [nome, setNome] = useState(local?.nome ?? '');
  const [tipo, setTipo] = useState(local?.tipo ?? 'AUDITÓRIO');
  const [capacidade, setCapacidade] = useState(local?.capacidade ? String(local.capacidade) : '');
  const [endereco, setEndereco] = useState(local?.endereco ?? '');
  const [custo, setCusto] = useState(local?.custo ? String(local.custo) : '');
  const [infraSel, setInfraSel] = useState<string[]>(parseInfra(local?.infraestrutura ?? ''));
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const toggleInfra = (item: string) => {
    setInfraSel((prev) =>
      prev.includes(item) ? prev.filter((i) => i !== item) : [...prev, item]
    );
  };

  const validate = () => {
    const e: Record<string, string> = {};
    if (!nome.trim()) e.nome = 'Nome é obrigatório.';
    if (!capacidade || isNaN(Number(capacidade)) || Number(capacidade) <= 0)
      e.capacidade = 'Capacidade deve ser maior que zero.';
    if (!endereco.trim()) e.endereco = 'Endereço é obrigatório.';
    if (!custo || isNaN(Number(custo)) || Number(custo) < 0)
      e.custo = 'Custo inválido.';
    if (infraSel.length === 0) e.infra = 'Selecione ao menos uma infraestrutura.';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);
    try {
      const dto: LocalCreateDTO = {
        nome: nome.trim(),
        capacidade: Number(capacidade),
        endereco: endereco.trim(),
        tipo,
        infraestrutura: joinInfra(infraSel),
        custo: Number(custo),
      };
      if (isEdit) {
        await editarLocal(local!.id, dto);
        toast.success('Local atualizado com sucesso!');
      } else {
        await cadastrarLocal(dto);
        toast.success('Local cadastrado com sucesso!');
      }
      onSaved();
      onBack();
    } catch (err: any) {
      toast.error(err?.message || 'Erro ao salvar local.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="loc-page">
      {/* Back */}
      <button className="loc-back-link" onClick={onBack}>
        ← Voltar para Locais
      </button>

      <div className="loc-form-card">
        <div className="loc-form-card-header">
          <div>
            <div className="loc-form-card-title-row">
              <span className="loc-pin-icon">📍</span>
              <h2 className="loc-form-card-title">
                {isEdit ? 'Editar Local' : 'Cadastrar Novo Local'}
              </h2>
            </div>
            <p className="loc-form-card-subtitle">
              {isEdit
                ? `Editando ${local!.id}`
                : 'Preencha as informações do local'}
            </p>
          </div>
          {isEdit && (
            <div className="loc-form-id-box">
              <span className="loc-form-id-label">ID do Local</span>
              <span className="loc-form-id-value">{local!.id}</span>
            </div>
          )}
        </div>

        <form onSubmit={handleSubmit} className="loc-form-body">
          {/* ID do Local (somente criação) */}
          {!isEdit && (
            <div className="loc-form-group">
              <label className="loc-label">ID do Local</label>
              <input
                type="text"
                className="loc-input loc-input-disabled"
                placeholder="Gerado automaticamente após salvar"
                disabled
              />
            </div>
          )}

          {/* Nome */}
          <div className="loc-form-group">
            <label className="loc-label">
              Nome do Local <span className="loc-required">*</span>
            </label>
            <input
              type="text"
              className={`loc-input ${errors.nome ? 'loc-input-error' : ''}`}
              placeholder="Ex: Auditório Principal Torre Norte"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
            />
            {errors.nome && <span className="loc-error-msg">{errors.nome}</span>}
          </div>

          {/* Tipo + Capacidade */}
          <div className="loc-form-row">
            <div className="loc-form-group">
              <label className="loc-label">
                Tipo <span className="loc-required">*</span>
              </label>
              <select
                className="loc-input"
                value={tipo}
                onChange={(e) => setTipo(e.target.value)}
              >
                {TIPOS.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </div>
            <div className="loc-form-group">
              <label className="loc-label">
                Capacidade (pessoas) <span className="loc-required">*</span>
              </label>
              <input
                type="number"
                className={`loc-input ${errors.capacidade ? 'loc-input-error' : ''}`}
                placeholder="Ex: 500"
                value={capacidade}
                onChange={(e) => setCapacidade(e.target.value)}
                min={1}
              />
              {errors.capacidade && <span className="loc-error-msg">{errors.capacidade}</span>}
            </div>
          </div>

          {/* Endereço */}
          <div className="loc-form-group">
            <label className="loc-label">
              Endereço Completo <span className="loc-required">*</span>
            </label>
            <input
              type="text"
              className={`loc-input ${errors.endereco ? 'loc-input-error' : ''}`}
              placeholder="Ex: Av. Paulista, 1234 – Centro, São Paulo/SP"
              value={endereco}
              onChange={(e) => setEndereco(e.target.value)}
            />
            {errors.endereco && <span className="loc-error-msg">{errors.endereco}</span>}
          </div>

          {/* Custo */}
          <div className="loc-form-group">
            <label className="loc-label">
              Custo por Dia (R$) <span className="loc-required">*</span>
            </label>
            <input
              type="number"
              className={`loc-input ${errors.custo ? 'loc-input-error' : ''}`}
              placeholder="Ex: 25000"
              value={custo}
              onChange={(e) => setCusto(e.target.value)}
              min={0}
            />
            {errors.custo && <span className="loc-error-msg">{errors.custo}</span>}
          </div>

          {/* Infraestrutura */}
          <div className="loc-form-group">
            <label className="loc-label">Infraestrutura disponível</label>
            {errors.infra && <span className="loc-error-msg">{errors.infra}</span>}
            <div className="loc-infra-grid">
              {INFRA_OPCOES.map((op) => {
                const checked = infraSel.includes(op);
                return (
                  <label
                    key={op}
                    className={`loc-infra-item ${checked ? 'loc-infra-checked' : ''}`}
                    onClick={() => toggleInfra(op)}
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => toggleInfra(op)}
                      style={{ accentColor: '#3b82f6' }}
                    />
                    <span>{op}</span>
                  </label>
                );
              })}
            </div>
          </div>

          {/* Actions */}
          <div className="loc-form-actions">
            <button type="button" className="loc-btn-outline" onClick={onBack}>
              Cancelar
            </button>
            <button type="submit" className="loc-btn-primary" disabled={saving}>
              <span>💾</span>
              {saving ? 'Salvando...' : isEdit ? 'Salvar Alterações' : 'Cadastrar Local'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Local Detail View
// ─────────────────────────────────────────────────────────────────────────────
interface LocalDetailProps {
  localId: string;
  onBack: () => void;
  onEdit: (local: LocalDTO) => void;
  onDesativar: (local: LocalDTO) => void;
  refreshTrigger: number;
}

function LocalDetail({ localId, onBack, onEdit, onDesativar, refreshTrigger }: LocalDetailProps) {
  const [local, setLocal] = useState<LocalDTO | null>(null);
  const [manutencoes, setManutencoes] = useState<ManutencaoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'info' | 'turnos' | 'avaliacao'>('info');
  const [showManutencaoModal, setShowManutencaoModal] = useState(false);
  const [editingManutencao, setEditingManutencao] = useState<ManutencaoDTO | null>(null);
  const [deletingManutencaoId, setDeletingManutencaoId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [l, m] = await Promise.all([
        buscarLocal(localId),
        listarManutencoes(localId),
      ]);
      setLocal(l);
      setManutencoes(m);
    } catch {
      toast.error('Erro ao carregar dados do local.');
    } finally {
      setLoading(false);
    }
  }, [localId]);

  useEffect(() => {
    load();
  }, [load, refreshTrigger]);

  const handleRemoverManutencao = async (manutId: string) => {
    setDeletingManutencaoId(manutId);
    try {
      await removerManutencao(localId, manutId);
      toast.success('Manutenção removida.');
      setManutencoes((prev) => prev.filter((m) => m.id !== manutId));
      load();
    } catch (err: any) {
      toast.error(err?.message || 'Erro ao remover manutenção.');
    } finally {
      setDeletingManutencaoId(null);
    }
  };

  if (loading) {
    return (
      <div className="loc-page loc-loading">
        <div className="loc-spinner" />
        <p>Carregando local...</p>
      </div>
    );
  }

  if (!local) {
    return (
      <div className="loc-page">
        <button className="loc-back-link" onClick={onBack}>← Voltar para Locais</button>
        <p style={{ color: '#6b7280' }}>Local não encontrado.</p>
      </div>
    );
  }

  const infras = parseInfra(local.infraestrutura);
  const emManutencao = isEmManutencao(local);
  const manutAtiva = manutencoes.find(
    (m) => new Date(m.dataInicio) <= new Date() && new Date(m.dataFim) >= new Date()
  );

  return (
    <div className="loc-page">
      {/* Back */}
      <button className="loc-back-link" onClick={onBack}>
        ← Voltar para Locais
      </button>

      {/* Header */}
      <div className="loc-detail-header-row">
        <div>
          <p className="loc-detail-tipo">{local.tipo}</p>
          <h1 className="loc-detail-nome">{local.nome}</h1>
          <div className="loc-detail-status-row">
            <StatusBadge status={local.status} />
          </div>
        </div>
        <div className="loc-detail-actions">
          <button
            className="loc-btn-edit"
            onClick={() => onEdit(local)}
          >
            ✏ Editar
          </button>
          {local.status !== 'INATIVO' && (
            <button
              className="loc-btn-desativar"
              onClick={() => onDesativar(local)}
            >
              🚫 Desativar
            </button>
          )}
        </div>
      </div>

      {/* Manutencao Alert Banner */}
      {emManutencao && manutAtiva && (
        <div className="loc-manut-banner">
          <span className="loc-manut-banner-icon">🔧</span>
          <div>
            <p className="loc-manut-banner-title">
              Local em manutenção — indisponível para agendamentos
            </p>
            <p className="loc-manut-banner-sub">
              Manutenção em andamento de{' '}
              {formatDateShort(manutAtiva.dataInicio)} a{' '}
              {formatDateShort(manutAtiva.dataFim)} • Responsável:{' '}
              {manutAtiva.responsavel}
            </p>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="loc-detail-tabs">
        <button
          className={`loc-detail-tab ${activeTab === 'info' ? 'loc-tab-active' : ''}`}
          onClick={() => setActiveTab('info')}
        >
          ℹ Informações Gerais
        </button>
        <button
          className={`loc-detail-tab ${activeTab === 'turnos' ? 'loc-tab-active' : ''}`}
          onClick={() => setActiveTab('turnos')}
        >
          🕐 Turnos Operacionais
        </button>
        <button
          className={`loc-detail-tab ${activeTab === 'avaliacao' ? 'loc-tab-active' : ''}`}
          onClick={() => setActiveTab('avaliacao')}
        >
          📊 Avaliação de Desempenho
        </button>
      </div>

      {activeTab === 'info' && (
        <div className="loc-detail-card">
          {/* Info grid */}
          <div className="loc-detail-info-grid">
            <div className="loc-detail-info-item">
              <span className="loc-detail-info-icon">📍</span>
              <div>
                <p className="loc-detail-info-label">ENDEREÇO</p>
                <p className="loc-detail-info-value">{local.endereco}</p>
              </div>
            </div>
            <div className="loc-detail-info-item">
              <span className="loc-detail-info-icon">👥</span>
              <div>
                <p className="loc-detail-info-label">CAPACIDADE</p>
                <p className="loc-detail-info-value">{local.capacidade} pessoas</p>
              </div>
            </div>
            <div className="loc-detail-info-item">
              <span className="loc-detail-info-icon">💵</span>
              <div>
                <p className="loc-detail-info-label">CUSTO POR DIA</p>
                <p className="loc-detail-info-value">{formatCustoFull(local.custo)}</p>
              </div>
            </div>
            <div className="loc-detail-info-item">
              <span className="loc-detail-info-icon">🏷</span>
              <div>
                <p className="loc-detail-info-label">TIPO</p>
                <p className="loc-detail-info-value">{local.tipo}</p>
              </div>
            </div>
          </div>

          {/* Infraestrutura */}
          <div className="loc-detail-infra-section">
            <p className="loc-detail-info-label" style={{ marginBottom: '0.5rem' }}>
              INFRAESTRUTURA
            </p>
            <div className="loc-infra-tags">
              {infras.map((i) => (
                <span key={i} className="loc-infra-tag">
                  {i}
                </span>
              ))}
            </div>
          </div>

          {/* Datas */}
          <div className="loc-detail-dates">
            {local.createdAt && (
              <span>Criado em: {formatDate(local.createdAt)}</span>
            )}
            {local.updatedAt && (
              <span>Atualizado em: {formatDate(local.updatedAt)}</span>
            )}
          </div>

          {/* Reservas Aprovadas */}
          <div className="loc-detail-section">
            <div className="loc-detail-section-header">
              <span>📋</span>
              <h3>Reservas Aprovadas Cadastradas</h3>
            </div>
            {local.reservas && local.reservas.length > 0 ? (
              local.reservas.map((r) => (
                <div key={r.id} className="loc-reserva-item">
                  <div>
                    <p className="loc-reserva-nome">{r.eventoNome}</p>
                    <p className="loc-reserva-datas">
                      {formatDateShort(r.dataInicio)} — {formatDateShort(r.dataFim)}
                    </p>
                  </div>
                  <span
                    className={`loc-reserva-status ${r.status === 'APROVADA' ? 'loc-reserva-aprovada' : ''}`}
                  >
                    {r.status}
                  </span>
                </div>
              ))
            ) : (
              <div className="loc-empty-small">Nenhuma reserva aprovada.</div>
            )}
          </div>

          {/* Manutenções */}
          <div className="loc-detail-section">
            <div className="loc-detail-section-header">
              <div>
                <span>🔧</span>
                <h3>Manutenções {manutencoes.length > 0 ? manutencoes.length : ''}</h3>
              </div>
              <button
                className="loc-btn-add-manut"
                onClick={() => {
                  setEditingManutencao(null);
                  setShowManutencaoModal(true);
                }}
              >
                + Registrar Manutenção
              </button>
            </div>

            {manutencoes.length === 0 ? (
              <div className="loc-empty-manut">
                <div className="loc-empty-manut-icon">🔧</div>
                <p>Nenhuma manutenção registrada</p>
                <small>Registre períodos de manutenção para bloquear agendamentos.</small>
              </div>
            ) : (
              <div className="loc-manut-list">
                {manutencoes.map((m) => {
                  const now = new Date();
                  const inicio = new Date(m.dataInicio);
                  const fim = new Date(m.dataFim);
                  const emAndamento = inicio <= now && fim >= now;
                  return (
                    <div key={m.id} className="loc-manut-card">
                      <div className="loc-manut-card-header">
                        <div className="loc-manut-card-id-row">
                          <span className="loc-manut-id">MNT-{m.id.slice(0, 5).toUpperCase()}</span>
                          <span
                            className={`loc-manut-status ${
                              emAndamento ? 'loc-manut-andamento' : 'loc-manut-agendada'
                            }`}
                          >
                            {emAndamento ? 'Em andamento' : 'Agendada'}
                          </span>
                        </div>
                        <div className="loc-manut-card-btns">
                          <button
                            className="loc-icon-btn"
                            title="Editar"
                            onClick={() => {
                              setEditingManutencao(m);
                              setShowManutencaoModal(true);
                            }}
                          >
                            ✏
                          </button>
                          <button
                            className="loc-icon-btn loc-icon-btn-danger"
                            title="Remover"
                            disabled={deletingManutencaoId === m.id}
                            onClick={() => handleRemoverManutencao(m.id)}
                          >
                            🗑
                          </button>
                        </div>
                      </div>
                      <p className="loc-manut-datas">
                        📅 {formatDateShort(m.dataInicio)} — {formatDateShort(m.dataFim)}
                      </p>
                      <p className="loc-manut-resp">
                        👤 {m.responsavel}
                      </p>
                      {m.descricao && (
                        <p className="loc-manut-desc">{m.descricao}</p>
                      )}
                      {m.createdAt && (
                        <p className="loc-manut-reg">
                          ⏱ Registrado em {formatDate(m.createdAt)}
                          {m.registradoPor ? ` por ${m.registradoPor}` : ''}
                        </p>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'turnos' && (
        <div className="loc-detail-card">
          <div className="loc-empty-tab">
            <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>🕐</div>
            <p>Nenhum turno operacional cadastrado para este local.</p>
          </div>
        </div>
      )}

      {activeTab === 'avaliacao' && (
        <div className="loc-detail-card">
          <div className="loc-empty-tab">
            <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>📊</div>
            <p>Nenhuma avaliação de desempenho disponível.</p>
          </div>
        </div>
      )}

      {/* Manutencao Modal */}
      {showManutencaoModal && (
        <ManutencaoModal
          localId={localId}
          manutencao={editingManutencao}
          onClose={() => setShowManutencaoModal(false)}
          onSaved={load}
        />
      )}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Locais List View
// ─────────────────────────────────────────────────────────────────────────────
interface LocaisListProps {
  onViewDetail: (id: string) => void;
  onCreateNew: () => void;
  onEdit: (local: LocalDTO) => void;
  refreshTrigger: number;
}

function LocaisList({ onViewDetail, onCreateNew, onEdit, refreshTrigger }: LocaisListProps) {
  const [locais, setLocais] = useState<LocalDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('todos');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listarLocais();
      setLocais(data);
    } catch (err: any) {
      toast.error(err?.message || 'Erro ao listar locais.');
      setLocais([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load, refreshTrigger]);

  const ativos = locais.filter((l) => l.status === 'ATIVO').length;
  const emManutHoje = locais.filter((l) => l.status === 'EM_MANUTENCAO').length;
  const inativos = locais.filter((l) => l.status === 'INATIVO').length;

  const filtered = locais.filter((l) => {
    const matchSearch =
      !search ||
      l.nome.toLowerCase().includes(search.toLowerCase()) ||
      l.id.toLowerCase().includes(search.toLowerCase());
    const matchStatus =
      statusFilter === 'todos' ||
      (statusFilter === 'ativo' && l.status === 'ATIVO') ||
      (statusFilter === 'inativo' && l.status === 'INATIVO') ||
      (statusFilter === 'manutencao' && l.status === 'EM_MANUTENCAO');
    return matchSearch && matchStatus;
  });

  return (
    <div className="loc-page">
      {/* Page Header */}
      <div className="loc-page-header">
        <div>
          <h1 className="loc-page-title">Gestão de Locais</h1>
          <p className="loc-page-subtitle">
            Cadastro, edição, desativação e manutenções de locais
          </p>
        </div>
        <button className="loc-btn-primary loc-btn-create" onClick={onCreateNew}>
          + Cadastrar Local
        </button>
      </div>

      {/* Summary Cards */}
      <div className="loc-summary-grid">
        <div className="loc-summary-card">
          <div>
            <p className="loc-summary-label">Locais Ativos</p>
            <p className="loc-summary-value">{ativos}</p>
          </div>
          <span className="loc-summary-icon loc-icon-green">✅</span>
        </div>
        <div className="loc-summary-card">
          <div>
            <p className="loc-summary-label">Em Manutenção (hoje)</p>
            <p className="loc-summary-value">{emManutHoje}</p>
          </div>
          <span className="loc-summary-icon loc-icon-orange">🔧</span>
        </div>
        <div className="loc-summary-card">
          <div>
            <p className="loc-summary-label">Inativos (histórico)</p>
            <p className="loc-summary-value">{inativos}</p>
          </div>
          <span className="loc-summary-icon loc-icon-gray">⊘</span>
        </div>
      </div>

      {/* Filters */}
      <div className="loc-filters-row">
        <div className="loc-search-wrapper">
          <span className="loc-search-icon">🔍</span>
          <input
            type="text"
            className="loc-search-input"
            placeholder="Buscar por nome ou ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <select
          className="loc-filter-select"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="todos">Todos os status</option>
          <option value="ativo">Ativo</option>
          <option value="inativo">Inativo</option>
          <option value="manutencao">Em Manutenção</option>
        </select>
      </div>

      {/* Table */}
      <div className="loc-table-container">
        {loading ? (
          <div className="loc-loading-row">
            <div className="loc-spinner" />
            <p>Carregando locais...</p>
          </div>
        ) : filtered.length === 0 ? (
          <div className="loc-empty-row">
            <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>🏛</div>
            <p>Nenhum local encontrado.</p>
          </div>
        ) : (
          <table className="loc-table">
            <thead>
              <tr>
                <th>ID / NOME</th>
                <th>TIPO</th>
                <th>CAPACIDADE</th>
                <th>ENDEREÇO</th>
                <th>INFRAESTRUTURA</th>
                <th>CUSTO/DIA</th>
                <th>STATUS</th>
                <th>ATUALIZADO</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((local) => {
                const infras = parseInfra(local.infraestrutura);
                const infrasVisiveis = infras.slice(0, 2);
                const extras = infras.length - 2;
                return (
                  <tr key={local.id} className="loc-table-row">
                    {/* ID / Nome */}
                    <td>
                      <button
                        className="loc-table-id"
                        onClick={() => onViewDetail(local.id)}
                      >
                        {local.id || genLocalId()}
                      </button>
                      <p className="loc-table-nome">{local.nome}</p>
                    </td>

                    {/* Tipo */}
                    <td>
                      <span className="loc-tipo-badge">{local.tipo}</span>
                    </td>

                    {/* Capacidade */}
                    <td>
                      <span className="loc-cap">👥 {local.capacidade}</span>
                    </td>

                    {/* Endereço */}
                    <td>
                      <span className="loc-endereco">
                        📍{' '}
                        {local.endereco.length > 20
                          ? local.endereco.slice(0, 20) + '...'
                          : local.endereco}
                      </span>
                    </td>

                    {/* Infraestrutura */}
                    <td>
                      <div className="loc-infra-cell">
                        {infrasVisiveis.map((i) => (
                          <span key={i} className="loc-infra-tag-sm">
                            {i}
                          </span>
                        ))}
                        {extras > 0 && (
                          <span className="loc-infra-tag-sm loc-infra-extra">
                            +{extras}
                          </span>
                        )}
                      </div>
                    </td>

                    {/* Custo */}
                    <td>
                      <span className="loc-custo">
                        $ {formatCusto(local.custo)}
                      </span>
                    </td>

                    {/* Status */}
                    <td>
                      <StatusBadge status={local.status} />
                    </td>

                    {/* Atualizado */}
                    <td className="loc-table-date">
                      {local.updatedAt ? formatDate(local.updatedAt) : '—'}
                    </td>

                    {/* Actions */}
                    <td>
                      {local.status !== 'INATIVO' && (
                        <div className="loc-table-btns">
                          <button
                            className="loc-icon-btn"
                            title="Visualizar"
                            onClick={() => onViewDetail(local.id)}
                          >
                            👁
                          </button>
                          <button
                            className="loc-icon-btn"
                            title="Editar"
                            onClick={() => onEdit(local)}
                          >
                            ✏
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* Footer */}
      {!loading && (
        <p className="loc-table-footer">
          {filtered.length} de {locais.length} local(is)
        </p>
      )}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Main LocaisApp
// ─────────────────────────────────────────────────────────────────────────────
type LocaisView =
  | { type: 'list' }
  | { type: 'create' }
  | { type: 'edit'; local: LocalDTO }
  | { type: 'detail'; localId: string };

export default function LocaisApp() {
  const [view, setView] = useState<LocaisView>({ type: 'list' });
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [desativarLocal_, setDesativarLocal] = useState<LocalDTO | null>(null);

  const refresh = () => setRefreshTrigger((n) => n + 1);

  const handleDesativar = async (local: LocalDTO) => {
    setDesativarLocal(local);
  };

  const confirmDesativar = async () => {
    if (!desativarLocal_) return;
    try {
      await desativarLocal(desativarLocal_.id);
      toast.success('Local desativado com sucesso.');
      setDesativarLocal(null);
      refresh();
      setView({ type: 'list' });
    } catch (err: any) {
      toast.error(err?.message || 'Erro ao desativar local.');
    }
  };

  return (
    <>
      {view.type === 'list' && (
        <LocaisList
          onViewDetail={(id) => setView({ type: 'detail', localId: id })}
          onCreateNew={() => setView({ type: 'create' })}
          onEdit={(local) => setView({ type: 'edit', local })}
          refreshTrigger={refreshTrigger}
        />
      )}

      {view.type === 'create' && (
        <LocalForm
          onBack={() => setView({ type: 'list' })}
          onSaved={refresh}
        />
      )}

      {view.type === 'edit' && (
        <LocalForm
          local={view.local}
          onBack={() => setView({ type: 'list' })}
          onSaved={refresh}
        />
      )}

      {view.type === 'detail' && (
        <LocalDetail
          localId={view.localId}
          onBack={() => setView({ type: 'list' })}
          onEdit={(local) => setView({ type: 'edit', local })}
          onDesativar={handleDesativar}
          refreshTrigger={refreshTrigger}
        />
      )}

      {desativarLocal_ && (
        <ConfirmDesativarModal
          local={desativarLocal_}
          onCancel={() => setDesativarLocal(null)}
          onConfirm={confirmDesativar}
        />
      )}
    </>
  );
}
