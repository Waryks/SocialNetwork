package socialnetwork.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph {
    int V;
    LinkedList<Integer>[] adjListArray;

    public Graph(int V) {
        this.V = V;

        adjListArray = new LinkedList[V];
        for (int i = 1; i < V; i++) {
            adjListArray[i] = new LinkedList<Integer>();
        }
    }

    public void addEdge(int src, int dest) {
        adjListArray[src].add(dest);
        adjListArray[dest].add(src);
    }

    public void DFSUtil(int v, boolean[] visited) {
        visited[v] = true;
        for (int x : adjListArray[v]) {
            if (!visited[x]) DFSUtil(x, visited);
        }

    }

    public int connectedComponents() {
        int nr = 0;
        boolean[] visited = new boolean[V];
        for (int v = 1; v < V; ++v) {
            if (!visited[v]) {
                DFSUtil(v, visited);
                nr++;
            }
        }
        return nr;
    }

    public int DFSUtil_Max(int v, boolean[] visited,int size) {
        size ++;
        visited[v] = true;
        for (int x : adjListArray[v]) {
            if (!visited[x])
                size = DFSUtil_Max(x, visited, size);
        }
        return size;
    }

    public void connectedComponents_Max() {
        int nr = 0, component = 0, max = 0, size = 0;
        boolean[] visited = new boolean[V];
        for (int v = 1; v < V; ++v) {
            size = 0;
            if (!visited[v]) {
                size = DFSUtil_Max(v, visited, size);
                if(max < size){
                    max = size;
                    component = v;
                }
            }
        }
        System.out.println("Biggest component is " + component + " with the size of " + max);
    }
}