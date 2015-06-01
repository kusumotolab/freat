package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class Node{
    public String name;
    public int group;
}

class Link{
    public int source;
    public int target;
    public int value;
}

public class DataLoader{
	
    public static Map<String,Object> getTwoData(){
        Map<String,Object> data = new HashMap<>();
        List<Node> nodes = new ArrayList<>();

        Node node = new Node();
        node.name = "abc";
        node.group = 1;
        nodes.add(node);

        node = new Node();
        node.name = "xyz";
        node.group = 2;
        nodes.add(node);

        List<Link> links = new ArrayList<>();
        Link link= new Link();
        link.source = 0;
        link.target = 1;
        link.value = 1;
        links.add(link);

        data.put("nodes", nodes);
        data.put("links", links);

        return data;
    }

    public static Map<String,Object> getThreeData(){
        Map<String,Object> data = new HashMap<>();
        List<Node> nodes = new ArrayList<>();

        Node node = new Node();
        node.name = "abc";
        node.group = 1;
        nodes.add(node);

        node = new Node();
        node.name = "xyz";
        node.group = 2;
        nodes.add(node);

        node = new Node();
        node.name = "foo";
        node.group = 3;
        nodes.add(node);

        List<Link> links = new ArrayList<>();
        Link link= new Link();
        link.source = 0;
        link.target = 1;
        link.value = 1;
        links.add(link);

        link= new Link();
        link.source = 0;
        link.target = 2;
        link.value = 1;
        links.add(link);

        link= new Link();
        link.source = 1;
        link.target = 2;
        link.value = 1;
        links.add(link);

        data.put("nodes", nodes);
        data.put("links", links);

        return data;
    }


}