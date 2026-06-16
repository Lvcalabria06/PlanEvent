import { useMemo, useCallback, useEffect } from "react";
import {
  ReactFlow,
  Background,
  Controls,
  type Edge,
  type Node,
  Position,
  useNodesState,
  useEdgesState,
  Panel,
  Handle,
} from "@xyflow/react";
import dagre from "dagre";
import "@xyflow/react/dist/style.css";
import type { Task } from "../contexts/TasksContext";

const nodeWidth = 250;
const nodeHeight = 80;

const getLayoutedElements = (nodes: Node[], edges: Edge[], direction = "TB") => {
  const dagreGraph = new dagre.graphlib.Graph();
  dagreGraph.setDefaultEdgeLabel(() => ({}));

  const isHorizontal = direction === "LR";
  dagreGraph.setGraph({ rankdir: direction });

  nodes.forEach((node) => {
    dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
  });

  edges.forEach((edge) => {
    dagreGraph.setEdge(edge.source, edge.target);
  });

  dagre.layout(dagreGraph);

  const newNodes = nodes.map((node) => {
    const nodeWithPosition = dagreGraph.node(node.id);
    const newNode = { ...node };

    newNode.targetPosition = isHorizontal ? Position.Left : Position.Top;
    newNode.sourcePosition = isHorizontal ? Position.Right : Position.Bottom;

    newNode.position = {
      x: nodeWithPosition.x - nodeWidth / 2,
      y: nodeWithPosition.y - nodeHeight / 2,
    };

    return newNode;
  });

  return { nodes: newNodes, edges };
};

const statusConfig = {
  completed: { color: "text-green-600", bg: "bg-green-50", border: "border-green-400" },
  in_progress: { color: "text-blue-600", bg: "bg-blue-50", border: "border-blue-400" },
  pending: { color: "text-gray-600", bg: "bg-gray-50", border: "border-gray-300" },
  overdue: { color: "text-red-600", bg: "bg-red-50", border: "border-red-400" },
  blocked: { color: "text-orange-600", bg: "bg-orange-50", border: "border-orange-400" },
};

function CustomTaskNode({ data }: { data: any }) {
  const config = statusConfig[data.status as keyof typeof statusConfig];

  return (
    <div className={`px-4 py-3 rounded-lg border-2 w-[250px] bg-white shadow-sm flex flex-col gap-2 ${config.border}`}>
      <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-gray-400" />
      <div className="font-semibold text-sm text-gray-800 line-clamp-2" title={data.label}>
        {data.label}
      </div>
      <div className="flex justify-between items-center mt-auto">
        <span className={`text-[10px] px-2 py-0.5 rounded-full font-medium uppercase ${config.bg} ${config.color}`}>
          {data.statusLabel}
        </span>
        <span className="text-xs text-gray-500 font-medium">
          {data.dueDate ? new Date(data.dueDate + "T00:00:00").toLocaleDateString("pt-BR") : "—"}
        </span>
      </div>
      <Handle type="source" position={Position.Bottom} className="w-2 h-2 !bg-gray-400" />
    </div>
  );
}

const nodeTypes = {
  customTask: CustomTaskNode,
};

export function TaskFlowchart({ tasks, onTaskClick }: { tasks: Task[]; onTaskClick?: (id: string) => void }) {
  const { nodes: layoutedNodes, edges: layoutedEdges } = useMemo(() => {
    const computedNodes: Node[] = tasks.map((task) => {
      let statusLabel = "Pendente";
      if (task.status === "in_progress") statusLabel = "Em Andamento";
      if (task.status === "completed") statusLabel = "Concluída";
      if (task.status === "blocked") statusLabel = "Bloqueada";
      if (task.status === "overdue") statusLabel = "Atrasada";

      return {
        id: task.id,
        type: "customTask",
        data: {
          label: task.title,
          status: task.status,
          statusLabel,
          dueDate: task.dueDate,
        },
        position: { x: 0, y: 0 },
      };
    });

    const computedEdges: Edge[] = [];
    tasks.forEach((task) => {
      task.dependencies.forEach((depId) => {
        computedEdges.push({
          id: `e${depId}-${task.id}`,
          source: depId,
          target: task.id,
          type: "smoothstep",
          animated: task.status === "in_progress" || task.status === "pending",
          style: { stroke: "#94a3b8", strokeWidth: 2 },
        });
      });
    });

    return getLayoutedElements(computedNodes, computedEdges, "TB");
  }, [tasks]);

  const [nodes, setNodes, onNodesChange] = useNodesState(layoutedNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(layoutedEdges);

  useEffect(() => {
    setNodes(layoutedNodes);
    setEdges(layoutedEdges);
  }, [layoutedNodes, layoutedEdges, setNodes, setEdges]);

  const handleNodeClick = useCallback(
    (_: any, node: Node) => {
      if (onTaskClick) {
        onTaskClick(node.id);
      }
    },
    [onTaskClick]
  );

  return (
    <div className="w-full h-full bg-slate-50/50 rounded-xl border border-gray-200 overflow-hidden relative">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={handleNodeClick}
        nodeTypes={nodeTypes}
        fitView
        attributionPosition="bottom-right"
      >
        <Background gap={16} size={1} />
        <Controls />
        <Panel position="top-right" className="bg-white p-3 rounded-lg shadow-sm border border-gray-100 m-4 flex flex-col gap-2">
          <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">Legenda</div>
          <div className="flex items-center gap-2 text-sm"><div className="w-3 h-3 rounded-full bg-green-400"></div> Concluída</div>
          <div className="flex items-center gap-2 text-sm"><div className="w-3 h-3 rounded-full bg-blue-400"></div> Em Andamento</div>
          <div className="flex items-center gap-2 text-sm"><div className="w-3 h-3 rounded-full bg-gray-300"></div> Pendente</div>
          <div className="flex items-center gap-2 text-sm"><div className="w-3 h-3 rounded-full bg-orange-400"></div> Bloqueada</div>
          <div className="flex items-center gap-2 text-sm"><div className="w-3 h-3 rounded-full bg-red-400"></div> Atrasada</div>
        </Panel>
      </ReactFlow>
    </div>
  );
}
