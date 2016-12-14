import org.apache.commons.lang3.SerializationUtils;
import org.mozilla.javascript.*;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Example implements Serializable {

    public void run() throws Exception {
        Context context = Context.enter();
        context.setOptimizationLevel(-2);   // Use interpreter mode.

        ScriptableObject globalScope = context.initStandardObjects();
        globalScope.put("example", globalScope, Context.javaToJS(this, globalScope));

        NativeContinuation capturedContinuation = null;
        try {
            String scriptSource =
                new String(Files.readAllBytes(Paths.get("src/example.js")));

            Script script =
                context.compileString(scriptSource, "example", 1, null);

            context.executeScriptWithContinuations(script, globalScope);
        } catch (ContinuationPending continuationPending) {
            capturedContinuation = (NativeContinuation)(continuationPending.getContinuation());
        }

        Object copyContinuation = SerializationUtils.clone(capturedContinuation);
        context.resumeContinuation(copyContinuation, globalScope, "");

        copyContinuation = SerializationUtils.clone(capturedContinuation);
        context.resumeContinuation(copyContinuation, globalScope, "");

        Context.exit();
    }

    public void captureContinuation() {
        ContinuationPending continuationPending =
            Context.enter().captureContinuation();
        Context.exit();
        throw continuationPending;
    }

    public void print(Object i) {
        System.out.println(i.toString() + " ");
    }

    public static void main(String[] args) throws Exception {
        new Example().run();
    }
}