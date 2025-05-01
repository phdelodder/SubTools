package extensions.org.w3c.dom.Node;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@UtilityClass
@Extension
public class NodeExt {

    public static @Nullable String getAttribute(@This @Nullable Node node, String attribute) {
        if (node == null) {
            return null;
        }
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        Node attributeNode = attributes.getNamedItem(attribute);
        if (attributeNode == null) {
            return null;
        }
        return attributeNode.getNodeValue();
    }
}
