package lt.compiler.syntactic.def;

import lt.compiler.LineCol;
import lt.compiler.syntactic.*;
import lt.compiler.syntactic.pre.Modifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * define a method
 */
public class MethodDef implements Definition {
        public final String name;
        public final Set<Modifier> modifiers;
        public final AST.Access returnType;
        public final List<VariableDef> params;
        public final Set<AST.Anno> annos;
        public final List<Statement> body;

        private final LineCol lineCol;

        public MethodDef(String name, Set<Modifier> modifiers, AST.Access returnType, List<VariableDef> params, Set<AST.Anno> annos, List<Statement> body, LineCol lineCol) {
                this.name = name;
                this.lineCol = lineCol;
                this.modifiers = new HashSet<>(modifiers);
                this.returnType = returnType;
                this.params = params;
                this.annos = new HashSet<>(annos);
                this.body = body;
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder("MethodDef(");
                for (AST.Anno anno : annos) {
                        sb.append(anno).append("");
                }
                for (Modifier m : modifiers) {
                        sb.append(m).append(" ");
                }
                sb.append(name).append("(");
                boolean isFirst = true;
                for (VariableDef v : params) {
                        if (isFirst)
                                isFirst = false;
                        else
                                sb.append(",");
                        sb.append(v);
                }
                sb.append(")");
                if (returnType != null)
                        sb.append(":").append(returnType);

                sb.append(body);

                sb.append(")");
                return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                MethodDef methodDef = (MethodDef) o;

                if (!name.equals(methodDef.name)) return false;
                if (!modifiers.equals(methodDef.modifiers)) return false;
                if (returnType != null ? !returnType.equals(methodDef.returnType) : methodDef.returnType != null) return false;
                if (!params.equals(methodDef.params)) return false;
                if (!annos.equals(methodDef.annos)) return false;
                return body.equals(methodDef.body);
        }

        @Override
        public int hashCode() {
                int result = name.hashCode();
                result = 31 * result + modifiers.hashCode();
                result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
                result = 31 * result + params.hashCode();
                result = 31 * result + annos.hashCode();
                result = 31 * result + body.hashCode();
                return result;
        }

        @Override
        public LineCol line_col() {
                return lineCol;
        }
}
