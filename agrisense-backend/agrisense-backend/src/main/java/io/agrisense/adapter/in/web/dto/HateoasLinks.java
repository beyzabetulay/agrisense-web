package io.agrisense.adapter.in.web.dto;

import java.util.HashMap;
import java.util.Map;

public class HateoasLinks {
    private Map<String, Link> links = new HashMap<>();

    public HateoasLinks addLink(String rel, String href) {
        this.links.put(rel, new Link(href, rel));
        return this;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}
