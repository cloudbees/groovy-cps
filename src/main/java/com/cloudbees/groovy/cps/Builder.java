package com.cloudbees.groovy.cps;

import com.cloudbees.groovy.cps.impl.ArrayAccessBlock;
import com.cloudbees.groovy.cps.impl.AssertBlock;
import com.cloudbees.groovy.cps.impl.AssignmentBlock;
import com.cloudbees.groovy.cps.impl.AttributeAccessBlock;
import com.cloudbees.groovy.cps.impl.BlockScopedBlock;
import com.cloudbees.groovy.cps.impl.BreakBlock;
import com.cloudbees.groovy.cps.impl.ClosureBlock;
import com.cloudbees.groovy.cps.impl.ConstantBlock;
import com.cloudbees.groovy.cps.impl.ContinueBlock;
import com.cloudbees.groovy.cps.impl.DoWhileBlock;
import com.cloudbees.groovy.cps.impl.ElvisBlock;
import com.cloudbees.groovy.cps.impl.ExcrementOperatorBlock;
import com.cloudbees.groovy.cps.impl.ForInLoopBlock;
import com.cloudbees.groovy.cps.impl.ForLoopBlock;
import com.cloudbees.groovy.cps.impl.FunctionCallBlock;
import com.cloudbees.groovy.cps.impl.IfBlock;
import com.cloudbees.groovy.cps.impl.ListBlock;
import com.cloudbees.groovy.cps.impl.LocalVariableBlock;
import com.cloudbees.groovy.cps.impl.LogicalOpBlock;
import com.cloudbees.groovy.cps.impl.MapBlock;
import com.cloudbees.groovy.cps.impl.NewArrayBlock;
import com.cloudbees.groovy.cps.impl.NotBlock;
import com.cloudbees.groovy.cps.impl.PropertyAccessBlock;
import com.cloudbees.groovy.cps.impl.ReturnBlock;
import com.cloudbees.groovy.cps.impl.SequenceBlock;
import com.cloudbees.groovy.cps.impl.SourceLocation;
import com.cloudbees.groovy.cps.impl.StaticFieldBlock;
import com.cloudbees.groovy.cps.impl.SwitchBlock;
import com.cloudbees.groovy.cps.impl.ThrowBlock;
import com.cloudbees.groovy.cps.impl.TryCatchBlock;
import com.cloudbees.groovy.cps.impl.VariableDeclBlock;
import com.cloudbees.groovy.cps.impl.WhileBlock;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;

import java.util.Arrays;
import java.util.List;

import static com.cloudbees.groovy.cps.Block.*;
import static java.util.Arrays.*;

/**
 * Builder pattern for constructing {@link Block}s into a tree.
 *
 * For example, to build a {@link Block} that represents "1+1", you'd call {@code plus(one(),one())}
 *
 * @author Kohsuke Kawaguchi
 */
public class Builder {
    private MethodLocation loc;

    public Builder(MethodLocation loc) {
        this.loc = loc;
    }

    /**
     * Evaluate the given closure by passing this object as an argument.
     * Used to bind literal Builder to a local variable.
     */
    public Object with(Closure c) {
        return c.call(this);
    }

    private static final Block NULL = new ConstantBlock(null);
    private static final LValueBlock THIS = new LocalVariableBlock("this");

    public Block null_() {
        return NULL;
    }

    public Block noop() {
        return NOOP;
    }

    public Block constant(Object o) {
        return new ConstantBlock(o);
    }

    public Block zero() {
        return constant(0);
    }

    public Block one() {
        return constant(1);
    }

    public Block two() {
        return constant(2);
    }

    public Block true_() {
        return constant(true);
    }

    public Block false_() {
        return constant(false);
    }

    /**
     * { ... }
     */
    public Block block(Block... bodies) {
        if (bodies.length==0)   return NULL;

        Block e = bodies[0];
        for (int i=1; i<bodies.length; i++)
            e = sequence(e,bodies[i]);

        return blockScoped(e);
    }

    /**
     * Creates a block scope of variables around the given expression
     */
    private Block blockScoped(final Block exp) {
        return new BlockScopedBlock(exp);
    }

    /**
     * Like {@link #block(Block...)} but it doesn't create a new scope.
     *
     */
    public Block sequence(Block... bodies) {
        if (bodies.length==0)   return NULL;

        Block e = bodies[0];
        for (int i=1; i<bodies.length; i++)
            e = sequence(e,bodies[i]);

        return e;
    }

    public Block sequence(final Block exp1, final Block exp2) {
        return new SequenceBlock(exp1, exp2);
    }

    public Block sequence(Block b) {
        return b;
    }

    public Block closure(List<String> parameters, Block body) {
        return new ClosureBlock(parameters,body);
    }

    public LValueBlock localVariable(String name) {
        return new LocalVariableBlock(name);
    }

    public Block setLocalVariable(int line, final String name, final Block rhs) {
        return assign(line,localVariable(name),rhs);
    }

    public Block declareVariable(final Class type, final String name) {
        return new VariableDeclBlock(type, name);
    }

    public Block declareVariable(int line, Class type, String name, Block init) {
        return sequence(
            declareVariable(type,name),
            setLocalVariable(line,name, init));
    }

    public Block this_() {
        return THIS;
    }

    /**
     * Assignment operator to a local variable, such as "x += 3"
     */
    public Block localVariableAssignOp(int line, String name, String operator, Block rhs) {
        return setLocalVariable(line, name, functionCall(line, localVariable(name), operator, rhs));
    }

    /**
     * if (...) { ... } else { ... }
     */
    public Block if_(Block cond, Block then, Block els) {
        return new IfBlock(cond,then,els);
    }

    public Block if_(Block cond, Block then) {
        return if_(cond, then, NOOP);
    }

    /**
     * for (e1; e2; e3) { ... }
     */
    public Block forLoop(String label, Block e1, Block e2, Block e3, Block body) {
        return new ForLoopBlock(label, e1,e2,e3,body);
    }

    /**
     * for (x in col) { ... }
     */
    public Block forInLoop(int line, String label, Class type, String variable, Block collection, Block body) {
        return new ForInLoopBlock(loc(line), label,type,variable,collection,body);
    }

    public Block break_(String label) {
        if (label==null)    return BreakBlock.INSTANCE;
        return new BreakBlock(label);
    }

    public Block continue_(String label) {
        if (label==null)    return ContinueBlock.INSTANCE;
        return new ContinueBlock(label);
    }

    public Block while_(String label, Block cond, Block body) {
        return new WhileBlock(label,cond,body);
    }

    public Block doWhile(String label, Block body, Block cond) {
        return new DoWhileBlock(label,body,cond);
    }

    public Block tryCatch(Block body, Block finally_, CatchExpression... catches) {
        return tryCatch(body, asList(catches), finally_);
    }


    /**
     * try {
     *     ...
     * } catch (T v) {
     *     ...
     * } catch (T v) {
     *     ...
     * }
     */
    public Block tryCatch(final Block body, final List<CatchExpression> catches) {
        return tryCatch(body, catches, null);
    }

    public Block tryCatch(final Block body, final List<CatchExpression> catches, final Block finally_) {
        return new TryCatchBlock(catches, body, finally_);
    }

    /**
     * throw exp;
     */
    public Block throw_(int line, final Block exp) {
        return new ThrowBlock(loc(line),exp,false);
    }

    /**
     * Map literal: [ a:b, c:d, e:f ] ...
     *
     * We expect arguments to be multiple of two.
     */
    public Block map(Block... blocks) {
        return new MapBlock(blocks);
    }

    public Block map(List<Block> blocks) {
        return map(blocks.toArray(new Block[blocks.size()]));
    }

    public Block staticCall(int line, Class lhs, String name, Block... argExps) {
        return functionCall(line, constant(lhs), name, argExps);
    }

    public Block plus(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "plus", rhs);
    }

    public Block plusEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "plus");
    }

    public Block minus(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "minus", rhs);
    }

    public Block minusEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "minus");
    }

    public Block multiply(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"multiply",rhs);
    }

    public Block multiplyEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "multiply");
    }

    public Block div(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"div",rhs);
    }

    public Block divEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "div");
    }

    public Block intdiv(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"intdiv",rhs);
    }

    public Block intdivEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "intdiv");
    }

    public Block mod(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "mod", rhs);
    }

    public Block modEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "mod");
    }

    public Block power(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs, "power", rhs);
    }

    public Block powerEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "power");
    }

    public Block unaryMinus(int line, Block lhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "unaryMinus", lhs);
    }

    public Block unaryPlus(int line, Block lhs) {
        return staticCall(line,ScriptBytecodeAdapter.class, "unaryPlus", lhs);
    }

    public Block ternaryOp(Block cond, Block trueExp, Block falseExp) {
        return if_(cond, trueExp, falseExp);
    }

    /**
     * x ?: y
     */
    public Block elvisOp(Block cond, Block falseExp) {
        return new ElvisBlock(cond,falseExp);
    }

    public Block compareEqual(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "compareEqual", lhs, rhs);
    }

    public Block compareNotEqual(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "compareNotEqual", lhs, rhs);
    }

    public Block compareTo(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "compareTo", lhs, rhs);
    }

    public Block lessThan(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "compareLessThan", lhs, rhs);
    }

    public Block lessThanEqual(int line, Block lhs, Block rhs) {
        return staticCall(line,ScriptBytecodeAdapter.class,"compareLessThanEqual",lhs,rhs);
    }

    public Block greaterThan(int line, Block lhs, Block rhs) {
        return staticCall(line,ScriptBytecodeAdapter.class,"compareGreaterThan",lhs,rhs);
    }

    public Block greaterThanEqual(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "compareGreaterThanEqual", lhs, rhs);
    }

    /**
     * lhs =~ rhs
     */
    public Block findRegex(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "findRegex", lhs, rhs);
    }

    /**
     * lhs ==~ rhs
     */
    public Block matchRegex(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "matchRegex", lhs, rhs);
    }

    /**
     * lhs in rhs
     */
    public Block isCase(int line, Block lhs, Block rhs) {
        return staticCall(line, ScriptBytecodeAdapter.class, "isCase", lhs, rhs);
    }

    /**
     * lhs && rhs
     */
    public Block logicalAnd(int line, Block lhs, Block rhs) {
        return new LogicalOpBlock(lhs,rhs,true);
    }

    /**
     * lhs || rhs
     */
    public Block logicalOr(int line, Block lhs, Block rhs) {
        return new LogicalOpBlock(lhs,rhs,false);
    }

    /**
     * lhs << rhs
     */
    public Block leftShift(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "leftShift", rhs);
    }

    /**
     * lhs <<= rhs
     */
    public Block leftShiftEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "leftShift");
    }

    /**
     * lhs >> rhs
     */
    public Block rightShift(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "rightShift", rhs);
    }

    /**
     * lhs >>= rhs
     */
    public Block rightShiftEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "rightShift");
    }

    /**
     * lhs >>> rhs
     */
    public Block rightShiftUnsigned(int line, Block lhs, Block rhs) {
        return functionCall(line, lhs, "rightShiftUnsigned", rhs);
    }

    /**
     * lhs >>>= rhs
     */
    public Block rightShiftUnsignedEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "rightShiftUnsigned");
    }

    /**
     * !b
     */
    public Block not(int line, Block b) {
        return new NotBlock(b);
    }

    public Block bitwiseAnd(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"and",rhs);
    }

    public Block bitwiseAndEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "and");
    }

    public Block bitwiseOr(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"or",rhs);
    }

    public Block bitwiseOrEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "or");
    }

    public Block bitwiseXor(int line, Block lhs, Block rhs) {
        return functionCall(line,lhs,"xor",rhs);
    }

    public Block bitwiseXorEqual(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line), lhs, rhs, "xor");
    }

    public Block bitwiseNegation(int line, Block b) {
        return staticCall(line,ScriptBytecodeAdapter.class,"bitwiseNegate",b);
    }

    /**
     * ++x
     */
    public Block prefixInc(int line, LValueBlock body) {
        return new ExcrementOperatorBlock(loc(line),"next",true,body);
    }

    /**
     * --x
     */
    public Block prefixDec(int line, LValueBlock body) {
        return new ExcrementOperatorBlock(loc(line),"previous",true,body);
    }

    /**
     * x++
     */
    public Block postfixInc(int line, LValueBlock body) {
        return new ExcrementOperatorBlock(loc(line),"next",false,body);
    }

    /**
     * x--
     */
    public Block postfixDec(int line, LValueBlock body) {
        return new ExcrementOperatorBlock(loc(line),"previous",false,body);
    }

    /**
     * Cast to type.
     *
     * @param coerce
     *      True for "exp as type" cast. false for "(type)exp" cast.
     */
    public Block cast(int line, Block block, Class type, boolean coerce) {
        return staticCall(line,ScriptBytecodeAdapter.class,
                coerce ? "asType" : "castToType",
                block,constant(type));
    }

    public Block instanceOf(int line, Block value, Block type) {
        return functionCall(line,type,"isInstance",value);
    }

    /**
     * LHS.name(...)
     */
    public Block functionCall(int line, Block lhs, String name, Block... argExps) {
        return new FunctionCallBlock(loc(line), lhs, constant(name), false, argExps);
    }

    public Block functionCall(int line, Block lhs, Block name, boolean safe, Block... argExps) {
        return new FunctionCallBlock(loc(line), lhs, name, safe, argExps);
    }

    public Block assign(int line, LValueBlock lhs, Block rhs) {
        return new AssignmentBlock(loc(line),lhs,rhs, null);
    }

    public LValueBlock property(int line, Block lhs, String property) {
        return property(line, lhs, constant(property), false);
    }

    public LValueBlock property(int line, Block lhs, Block property, boolean safe) {
        return new PropertyAccessBlock(loc(line), lhs, property, safe);
    }

    public LValueBlock array(int line, Block lhs, Block index) {
        return new ArrayAccessBlock(loc(line),lhs,index);
    }

    public LValueBlock attribute(int line, Block lhs, Block property, boolean safe) {
        return new AttributeAccessBlock(loc(line), lhs, property, safe);
    }

    public LValueBlock staticField(int line, Class type, String name) {
        return new StaticFieldBlock(loc(line),type,name);
    }

    public Block setProperty(int line, Block lhs, String property, Block rhs) {
        return setProperty(line, lhs, constant(property), rhs);
    }

    public Block setProperty(int line, Block lhs, Block property, Block rhs) {
        return assign(line, property(line, lhs, property, false), rhs);
    }

    /**
     * Object instantiation.
     */
    public Block new_(int line, Class type, Block... argExps) {
        return new_(line,constant(type),argExps);
    }

    public Block new_(int line, Block type, Block... argExps) {
        return new FunctionCallBlock(loc(line), type, constant("<init>"), false, argExps);
    }

    /**
     * Array instantiation like {@code new String[1][5]}
     */
    public Block newArray(int line, Class type, Block... argExps) {
        return new NewArrayBlock(loc(line),type,argExps);
    }

    /**
     * return exp;
     */
    public Block return_(final Block exp) {
        return new ReturnBlock(exp);
    }

    /**
     * [a,b,c,d] that creates a List.
     */
    public Block list(Block... args) {
        return new ListBlock(args);
    }

    /**
     * x..y or x..&lt;y to crete a range
     */
    public Block range(int line, Block from, Block to, boolean inclusive) {
        return staticCall(line,ScriptBytecodeAdapter.class,"createRange",from,to,constant(inclusive));
    }

    public Block assert_(Block cond, Block msg, String sourceText) {
        return new AssertBlock(cond,msg,sourceText);
    }

    public Block assert_(Block cond, String sourceText) {
        return assert_(cond,null_(),sourceText);
    }

    /**
     * "Foo bar zot ${x}" kind of string
     */
    public Block gstring(int line, Block listOfValues, Block listOfStrings) {
        return new_(line, GStringImpl.class,
                cast(line,listOfValues, Object[].class,true),
                cast(line,listOfStrings,String[].class,true));
    }

    /**
     * @see #case_(int, Block, Block)
     */
    public Block switch_(String label, Block switchExp, Block defaultStmt, CaseExpression... caseExps) {
        return new SwitchBlock(label, switchExp, defaultStmt, Arrays.asList(caseExps));
    }

    public CaseExpression case_(int line, Block matcher, Block body) {
        return new CaseExpression(loc(line), matcher, body);
    }

    private SourceLocation loc(int line) {
        return new SourceLocation(loc,line);
    }
}
