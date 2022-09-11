package org.lodder.subtools.sublibrary.xml;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.experimental.UtilityClass;

@UtilityClass
public class XmlExtension {

    public static Stream<Node> stream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
    }
}
