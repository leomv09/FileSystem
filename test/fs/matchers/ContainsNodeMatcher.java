package fs.matchers;

import fs.Directory;
import fs.File;
import fs.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class ContainsNodeMatcher extends BaseMatcher<Collection<Node>> {

    private final List<Node> lines;

    private ContainsNodeMatcher(Node... lines) {
        this.lines = new ArrayList<>();
        this.lines.addAll(Arrays.asList(lines));
    }

    @Override
    public boolean matches(Object obj) {
        Collection<Node> list = (Collection<Node>) obj;
        return list.stream().anyMatch((l1) -> (lines.stream().anyMatch((l2) -> 
                (l1.getName().equals(l2.getName()) && l1.isDirectory() == l2.isDirectory()))));
    }

    @Override
    public void describeTo(Description d) {
        d.appendText("contains node " + lines.get(0));
    }
    
    @Factory
    public static <T> Matcher<Collection<Node>> containsNode(Node node) {
      return new ContainsNodeMatcher(node);
    }
    
    @Factory
    public static <T> Matcher<Collection<Node>> containsFile(String name) {
      return new ContainsNodeMatcher(new File(name));
    }
    
    @Factory
    public static <T> Matcher<Collection<Node>> containsDirectory(String name) {
      return new ContainsNodeMatcher(new Directory(name));
    }
    
}

