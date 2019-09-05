package graph;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CandidateCode {
    private final int V;
    private final List<List<Integer>> adj;

    public CandidateCode(int V) {
        this.V = V;
        adj = new ArrayList<>(V);

        for (int i = 0; i < V; i++)
            adj.add(new LinkedList<>());
    }

    //class 6
// Driver code by Vishwa Ratna
    public static void main(String[] args) throws IOException {
        List<Integer> lengthofCycles = new ArrayList<>();
        Map<String,Integer> tortoiseTimeMap = new HashMap<>();
        Map<String,Integer> hareTimeMap = new HashMap<>();
        List<CycleInfo> cycleInfos = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        String NandM = sc.nextLine();
        int hTotal = 0;
        int tTotal = 0;
        int maxSelfLoop = 0;
        List<Integer> startingHill = new ArrayList<>();
        List<Integer> endingHill = new ArrayList<>();
        List<Integer> tortoiseTime = new ArrayList<>();
        List<Integer> hareTime = new ArrayList<>();
        String[] NandMAsArray = NandM.split(" ");
        int N = Integer.parseInt(NandMAsArray[0]);
        int M = Integer.parseInt(NandMAsArray[1]);
        String nodes[] = new String[N];
        boolean adjMatrix[][] = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            nodes[i] = String.valueOf((char) ('1' + i));
        }
        String line;

        while (sc.hasNextLine()) {
            line = sc.nextLine();
            if (line.isEmpty()) {
                break;
            }
            String arr[] = line.split(" ");
            if (arr.length == 4) {

                startingHill.add(Integer.parseInt(arr[0]));
                endingHill.add(Integer.parseInt(arr[1]));
                tortoiseTime.add(Integer.parseInt(arr[2]));
                hareTime.add(Integer.parseInt(arr[3]));
                tortoiseTimeMap.put(arr[0]+arr[1],Integer.parseInt(arr[2]));
                hareTimeMap.put(arr[0]+arr[1],Integer.parseInt(arr[3]));
                int tTime = Integer.parseInt(arr[2]);
                int hTime = Integer.parseInt(arr[3]);
                if (arr[0].equals(arr[1])) {
                    if (hTime - tTime > maxSelfLoop) {
                        maxSelfLoop = hTime - tTime;
                    }
                }
            }
        }

        for (int i = 0; i < M; i++) {
            adjMatrix[startingHill.get(i) - 1][endingHill.get(i) - 1] = true;
        }
        ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(adjMatrix, nodes);
        List cycles = ecs.getElementaryCycles();
        //For calculating self loop but unfortunately none :)
        if (maxSelfLoop != 0) {
            System.out.println(1 + " " + maxSelfLoop);
        } else {
            for (int i = 0; i < cycles.size(); i++) {
                List cycle = (List) cycles.get(i);
                for (int j=0;j<cycle.size()-1;j++){
                    hTotal+= hareTimeMap.get(String.valueOf(cycle.get(j))+cycle.get(j+1));
                    tTotal+= tortoiseTimeMap.get(String.valueOf(cycle.get(j))+cycle.get(j+1));
                }
                hTotal+= hareTimeMap.get(String.valueOf(cycle.get(cycle.size()-1))+cycle.get(0));
                tTotal+= tortoiseTimeMap.get(String.valueOf(cycle.get(cycle.size()-1))+cycle.get(0));

                cycleInfos.add(new CycleInfo(cycle.size(), hTotal - tTotal));
                hTotal = 0;
                tTotal = 0;
                lengthofCycles.add(cycle.size());
            }
            List<CycleInfo> negativeRemovedCycleInfos = new ArrayList<>();
            negativeRemovedCycleInfos = cycleInfos.stream().filter(s->s.totalMargin>0).collect(Collectors.toList());
            negativeRemovedCycleInfos.sort(Comparator.comparingInt(CycleInfo::getLen).reversed().thenComparing(CycleInfo::getTotalMargin).reversed());
            System.out.println( negativeRemovedCycleInfos.get(0).len + " " + negativeRemovedCycleInfos.get(0).totalMargin);

        }

    }

    static class CycleInfo {
        private int len;
        private int totalMargin;

        public CycleInfo(int len, int totalMargin) {
            this.len = len;
            this.totalMargin = totalMargin;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public int getTotalMargin() {
            return totalMargin;
        }

        public void setTotalMargin(int totalMargin) {
            this.totalMargin = totalMargin;
        }

        @Override
        public String toString() {
            return "CycleInfo{" +
                    "len=" + len +
                    ", totalMargin=" + totalMargin +
                    '}';
        }
    }


    private boolean isCyclicUtil(int i, boolean[] visited, boolean[] recStack) {
// Mark the current node as visited and
// part of recursion stack
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        List<Integer> children = adj.get(i);

        for (Integer c : children)
            if (isCyclicUtil(c, visited, recStack))
                return true;

        recStack[i] = false;

        return false;
    }

    private void addEdge(int source, int dest) {
        adj.get(source).add(dest);
    }

    private boolean isCyclic() {
// Mark all the vertices as not visited and
// not part of recursion stack
        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];

// Call the recursive helper function to
// detect cycle in different DFS trees
        for (int i = 0; i < V; i++)
            if (isCyclicUtil(i, visited, recStack))
                return true;

        return false;
    }

//class 2

    //class 1
    static class ElementaryCyclesSearch {

        static class CycleLenMrg {
            private int len;
            private int totalMargin;

            public CycleLenMrg(int len, int totalMargin) {
                this.len = len;
                this.totalMargin = totalMargin;
            }
        }

        /**
         * List of cycles
         */
        private List cycles = null;
        private List<CycleLenMrg> cycleLenMrgs;
        /**
         * Adjacency-list of graph
         */
        private int[][] adjList = null;

        /**
         * Graphnodes
         */
        private Object[] graphNodes = null;

        /**
         * Blocked nodes, used by the algorithm of Johnson
         */
        private boolean[] blocked = null;

        /**
         * B-Lists, used by the algorithm of Johnson
         */
        private Vector[] B = null;

        /**
         * Stack for nodes, used by the algorithm of Johnson
         */
        private Vector stack = null;

        public ElementaryCyclesSearch(boolean[][] matrix, Object[] graphNodes) {
            this.graphNodes = graphNodes;
            this.adjList = AdjacencyList.getAdjacencyList(matrix);
        }

        public List getElementaryCycles() {
            this.cycles = new Vector();
            this.cycleLenMrgs = new ArrayList<>();
            this.blocked = new boolean[this.adjList.length];
            this.B = new Vector[this.adjList.length];
            this.stack = new Vector();
            StrongConnectedComponents sccs = new StrongConnectedComponents(this.adjList);
            int s = 0;

            while (true) {
                SCCResult sccResult = sccs.getAdjacencyList(s);
                if (sccResult != null && sccResult.getAdjList() != null) {
                    Vector[] scc = sccResult.getAdjList();
                    s = sccResult.getLowestNodeId();
                    for (int j = 0; j < scc.length; j++) {
                        if ((scc[j] != null) && (scc[j].size() > 0)) {
                            this.blocked[j] = false;
                            this.B[j] = new Vector();
                        }
                    }

                    this.findCycles(s, s, scc);
                    s++;
                } else {
                    break;
                }
            }

            return this.cycles;
        }

        private boolean findCycles(int v, int s, Vector[] adjList) {
            boolean f = false;
            this.stack.add(new Integer(v));
            this.blocked[v] = true;

            for (int i = 0; i < adjList[v].size(); i++) {
                int w = ((Integer) adjList[v].get(i)).intValue();
// found cycle
                if (w == s) {
                    Vector cycle = new Vector();
                    for (int j = 0; j < this.stack.size(); j++) {
                        int index = ((Integer) this.stack.get(j)).intValue();
                        cycle.add(this.graphNodes[index]);
                    }
                    this.cycles.add(cycle);
                    f = true;
                } else if (!this.blocked[w]) {
                    if (this.findCycles(w, s, adjList)) {
                        f = true;
                    }
                }
            }

            if (f) {
                this.unblock(v);
            } else {
                for (int i = 0; i < adjList[v].size(); i++) {
                    int w = ((Integer) adjList[v].get(i)).intValue();
                    if (!this.B[w].contains(new Integer(v))) {
                        this.B[w].add(new Integer(v));
                    }
                }
            }

            this.stack.remove(new Integer(v));
            return f;
        }

        private void unblock(int node) {
            this.blocked[node] = false;
            Vector Bnode = this.B[node];
            while (Bnode.size() > 0) {
                Integer w = (Integer) Bnode.get(0);
                Bnode.remove(0);
                if (this.blocked[w.intValue()]) {
                    this.unblock(w.intValue());
                }
            }
        }
    }

    static class StrongConnectedComponents {
        /**
         * Adjacency-list of original graph
         */
        private int[][] adjListOriginal = null;

        /**
         * Adjacency-list of currently viewed subgraph
         */
        private int[][] adjList = null;

        /**
         * Helpattribute for finding scc's
         */
        private boolean[] visited = null;

        /**
         * Helpattribute for finding scc's
         */
        private Vector stack = null;

        /**
         * Helpattribute for finding scc's
         */
        private int[] lowlink = null;

        /**
         * Helpattribute for finding scc's
         */
        private int[] number = null;

        /**
         * Helpattribute for finding scc's
         */
        private int sccCounter = 0;

        /**
         * Helpattribute for finding scc's
         */
        private Vector currentSCCs = null;

        /**
         * Constructor.
         *
         * @param adjList adjacency-list of the graph
         */
        public StrongConnectedComponents(int[][] adjList) {
            this.adjListOriginal = adjList;
        }

        public SCCResult getAdjacencyList(int node) {
            this.visited = new boolean[this.adjListOriginal.length];
            this.lowlink = new int[this.adjListOriginal.length];
            this.number = new int[this.adjListOriginal.length];
            this.visited = new boolean[this.adjListOriginal.length];
            this.stack = new Vector();
            this.currentSCCs = new Vector();

            this.makeAdjListSubgraph(node);

            for (int i = node; i < this.adjListOriginal.length; i++) {
                if (!this.visited[i]) {
                    this.getStrongConnectedComponents(i);
                    Vector nodes = this.getLowestIdComponent();
                    if (nodes != null && !nodes.contains(new Integer(node)) && !nodes.contains(new Integer(node + 1))) {
                        return this.getAdjacencyList(node + 1);
                    } else {
                        Vector[] adjacencyList = this.getAdjList(nodes);
                        if (adjacencyList != null) {
                            for (int j = 0; j < this.adjListOriginal.length; j++) {
                                if (adjacencyList[j].size() > 0) {
                                    return new SCCResult(adjacencyList, j);
                                }
                            }
                        }
                    }
                }
            }

            return null;
        }

        private void makeAdjListSubgraph(int node) {
            this.adjList = new int[this.adjListOriginal.length][0];

            for (int i = node; i < this.adjList.length; i++) {
                Vector successors = new Vector();
                for (int j = 0; j < this.adjListOriginal[i].length; j++) {
                    if (this.adjListOriginal[i][j] >= node) {
                        successors.add(new Integer(this.adjListOriginal[i][j]));
                    }
                }
                if (successors.size() > 0) {
                    this.adjList[i] = new int[successors.size()];
                    for (int j = 0; j < successors.size(); j++) {
                        Integer succ = (Integer) successors.get(j);
                        this.adjList[i][j] = succ.intValue();
                    }
                }
            }
        }

        private Vector getLowestIdComponent() {
            int min = this.adjList.length;
            Vector currScc = null;

            for (int i = 0; i < this.currentSCCs.size(); i++) {
                Vector scc = (Vector) this.currentSCCs.get(i);
                for (int j = 0; j < scc.size(); j++) {
                    Integer node = (Integer) scc.get(j);
                    if (node.intValue() < min) {
                        currScc = scc;
                        min = node.intValue();
                    }
                }
            }

            return currScc;
        }

        private Vector[] getAdjList(Vector nodes) {
            Vector[] lowestIdAdjacencyList = null;

            if (nodes != null) {
                lowestIdAdjacencyList = new Vector[this.adjList.length];
                for (int i = 0; i < lowestIdAdjacencyList.length; i++) {
                    lowestIdAdjacencyList[i] = new Vector();
                }
                for (int i = 0; i < nodes.size(); i++) {
                    int node = ((Integer) nodes.get(i)).intValue();
                    for (int j = 0; j < this.adjList[node].length; j++) {
                        int succ = this.adjList[node][j];
                        if (nodes.contains(new Integer(succ))) {
                            lowestIdAdjacencyList[node].add(new Integer(succ));
                        }
                    }
                }
            }

            return lowestIdAdjacencyList;
        }

        private void getStrongConnectedComponents(int root) {
            this.sccCounter++;
            this.lowlink[root] = this.sccCounter;
            this.number[root] = this.sccCounter;
            this.visited[root] = true;
            this.stack.add(new Integer(root));

            for (int i = 0; i < this.adjList[root].length; i++) {
                int w = this.adjList[root][i];
                if (!this.visited[w]) {
                    this.getStrongConnectedComponents(w);
                    this.lowlink[root] = Math.min(lowlink[root], lowlink[w]);
                } else if (this.number[w] < this.number[root]) {
                    if (this.stack.contains(new Integer(w))) {
                        lowlink[root] = Math.min(this.lowlink[root], this.number[w]);
                    }
                }
            }

// found scc
            if ((lowlink[root] == number[root]) && (stack.size() > 0)) {
                int next = -1;
                Vector scc = new Vector();

                do {
                    next = ((Integer) this.stack.get(stack.size() - 1)).intValue();
                    this.stack.remove(stack.size() - 1);
                    scc.add(new Integer(next));
                } while (this.number[next] > this.number[root]);

// simple scc's with just one node will not be added
                if (scc.size() > 1) {
                    this.currentSCCs.add(scc);
                }
            }
        }

    }

//class 4

    //class 3
    static class SCCResult {
        private Set nodeIDsOfSCC = null;
        private Vector[] adjList = null;
        private int lowestNodeId = -1;

        public SCCResult(Vector[] adjList, int lowestNodeId) {
            this.adjList = adjList;
            this.lowestNodeId = lowestNodeId;
            this.nodeIDsOfSCC = new HashSet();
            if (this.adjList != null) {
                for (int i = this.lowestNodeId; i < this.adjList.length; i++) {
                    if (this.adjList[i].size() > 0) {
                        this.nodeIDsOfSCC.add(new Integer(i));
                    }
                }
            }
        }

        public Vector[] getAdjList() {
            return adjList;
        }

        public int getLowestNodeId() {
            return lowestNodeId;
        }
    }

    static class AdjacencyList {
        public static int[][] getAdjacencyList(boolean[][] adjacencyMatrix) {
            int[][] list = new int[adjacencyMatrix.length][];

            for (int i = 0; i < adjacencyMatrix.length; i++) {
                Vector v = new Vector();
                for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                    if (adjacencyMatrix[i][j]) {
                        v.add(new Integer(j));
                    }
                }

                list[i] = new int[v.size()];
                for (int j = 0; j < v.size(); j++) {
                    Integer in = (Integer) v.get(j);
                    list[i][j] = in.intValue();
                }
            }

            return list;
        }
    }
}