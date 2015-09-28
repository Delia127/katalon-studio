package com.kms.katalon.composer.testcase.views;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;

public class TreeNodeBuildingVisitor extends CodeVisitorSupport {
	
	public TheAstTreeNode currentNode;
	private final GroovyScriptToTreeNodeAdapter adapter;
	private List<ASTNode> nodes = new ArrayList<ASTNode>();

	public TreeNodeBuildingVisitor(GroovyScriptToTreeNodeAdapter adapter) {
		if (adapter == null) throw new IllegalArgumentException("Null: adapter");
		this.adapter = adapter;
	}

	private void addNode(Object node, @SuppressWarnings("rawtypes") Class expectedSubclass, String superMethodName){
		try{
			if(node instanceof ASTNode){
				nodes.add((ASTNode)node);
			}
			if (expectedSubclass.getName() == node.getClass().getName()) {
				if (currentNode == null) {
					currentNode = (TheAstTreeNode)adapter.make(node);
					callSuper(superMethodName, node);
				} else {
					//visitor works off void methods... so we have to perform a swap to get accumulation like behavior.
					TheAstTreeNode temp = currentNode;
					currentNode = (TheAstTreeNode)adapter.make(node);

					temp.add(currentNode);
					currentNode.parent = temp;
					callSuper(superMethodName, node);
					currentNode = temp;
				}
			} 
			else {
				callSuper(superMethodName, node);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void callSuper(final String methodName, Object node){
		switch (methodName){
			case "visitBlockStatement":
				super.visitBlockStatement((BlockStatement)node);
				break;
			case "visitForLoop":
				super.visitForLoop((ForStatement)node);
				break;
			case "visitWhileLoop":
				super.visitWhileLoop((WhileStatement)node);
				break;
			case "visitDoWhileLoop":
				super.visitDoWhileLoop((DoWhileStatement)node);
				break;
			case "visitIfElse":
				super.visitIfElse((IfStatement)node);
				break;
			case "visitExpressionStatement":
				super.visitExpressionStatement((ExpressionStatement)node);
				break;
			case "visitReturnStatement":
				super.visitReturnStatement((ReturnStatement)node);
				break;
			case "visitAssertStatement":
				super.visitAssertStatement((AssertStatement)node);
				break;
			case "visitTryCatchFinally":
				super.visitTryCatchFinally((TryCatchStatement)node);
				break;
			case "visitEmptyStatement":
				super.visitEmptyStatement((EmptyStatement)node);
				break;
			case "visitSwitch":
				super.visitSwitch((SwitchStatement)node);
				break;
			case "visitCaseStatement":
				super.visitCaseStatement((CaseStatement)node);
				break;
			case "visitBreakStatement":
				super.visitBreakStatement((BreakStatement)node);
				break;
			case "visitContinueStatement":
				super.visitContinueStatement((ContinueStatement)node);
				break;
			case "visitSynchronizedStatement":
				super.visitSynchronizedStatement((SynchronizedStatement)node);
				break;
			case "visitThrowStatement":
				super.visitThrowStatement((ThrowStatement)node);
				break;
			case "visitMethodCallExpression":
				super.visitMethodCallExpression((MethodCallExpression)node);
				break;
			case "visitStaticMethodCallExpression":
				super.visitStaticMethodCallExpression((StaticMethodCallExpression)node);
				break;
			case "visitConstructorCallExpression":
				super.visitConstructorCallExpression((ConstructorCallExpression)node);
				break;
			case "visitBinaryExpression":
				super.visitBinaryExpression((BinaryExpression)node);
				break;
			case "visitTernaryExpression":
				super.visitTernaryExpression((TernaryExpression)node);
				break;
			case "visitShortTernaryExpression":
				super.visitShortTernaryExpression((ElvisOperatorExpression)node);
				break;
			case "visitPrefixExpression":
				super.visitPrefixExpression((PrefixExpression)node);
				break;
			case "visitBooleanExpression":
				super.visitBooleanExpression((BooleanExpression)node);
				break;
			case "visitNotExpression":
				super.visitNotExpression((NotExpression)node);
				break;
			case "visitTupleExpression":
				super.visitTupleExpression((TupleExpression)node);
				break;
			case "visitListExpression":
				super.visitListExpression((ListExpression)node);
				break;
			case "visitArrayExpression":
				super.visitArrayExpression((ArrayExpression)node);
				break;
			case "visitMapExpression":
				super.visitMapExpression((MapExpression)node);
				break;
			case "visitRangeExpression":
				super.visitRangeExpression((RangeExpression)node);
				break;
			case "visitSpreadExpression":
				super.visitSpreadExpression((SpreadExpression)node);
				break;
			case "visitSpreadMapExpression":
				super.visitSpreadMapExpression((SpreadMapExpression)node);
				break;
			case "visitMethodPointerExpression":
				super.visitMethodPointerExpression((MethodPointerExpression)node);
				break;
			case "visitUnaryMinusExpression":
				super.visitUnaryMinusExpression((UnaryMinusExpression)node);
				break;
			case "visitUnaryPlusExpression":
				super.visitUnaryPlusExpression((UnaryPlusExpression)node);
				break;
			case "visitBitwiseNegationExpression":
				super.visitBitwiseNegationExpression((BitwiseNegationExpression)node);
				break;
			case "visitCastExpression":
				super.visitCastExpression((CastExpression)node);
				break;
			case "visitConstantExpression":
				super.visitConstantExpression((ConstantExpression)node);
				break;
			case "visitClassExpression":
				super.visitClassExpression((ClassExpression)node);
				break;
			case "visitDeclarationExpression":
				super.visitDeclarationExpression((DeclarationExpression)node);
				break;
			case "visitPropertyExpression":
				super.visitPropertyExpression((PropertyExpression)node);
				break;
			case "visitAttributeExpression":
				super.visitAttributeExpression((AttributeExpression)node);
				break;
			case "visitFieldExpression":
				super.visitFieldExpression((FieldExpression)node);
				break;
			case "visitGStringExpression":
				super.visitGStringExpression((GStringExpression)node);
				break;
			case "visitArgumentlistExpression":
				super.visitArgumentlistExpression((ArgumentListExpression)node);
				break;
			case "visitClosureListExpression":
				super.visitClosureListExpression((ClosureListExpression)node);
				break;
			case "visitBytecodeExpression":
				super.visitBytecodeExpression((BytecodeExpression)node);
				break;
				
		}		
	}
	
	public void visitBlockStatement(BlockStatement node) {
		addNode(node, BlockStatement.class, "visitBlockStatement");
	}

	public void visitForLoop(ForStatement node) {
		addNode(node, ForStatement.class, "visitForLoop");
	}

	public void visitWhileLoop(WhileStatement node) {
		addNode(node, WhileStatement.class, "visitWhileLoop");
	}

	public void visitDoWhileLoop(DoWhileStatement node) {
		addNode(node, DoWhileStatement.class, "visitDoWhileLoop");
	}

	public void visitIfElse(IfStatement node) {
		addNode(node, IfStatement.class, "visitIfElse");
	}

	public void visitExpressionStatement(ExpressionStatement node) {
		addNode(node, ExpressionStatement.class, "visitExpressionStatement");
	}

	public void visitReturnStatement(ReturnStatement node) {
		addNode(node, ReturnStatement.class, "visitReturnStatement");
	}

	public void visitAssertStatement(AssertStatement node) {
		addNode(node, AssertStatement.class, "visitAssertStatement");
	}

	public void visitTryCatchFinally(TryCatchStatement node) {
		addNode(node, TryCatchStatement.class, "visitTryCatchFinally");
	}
	
	public void visitEmptyStatement(EmptyStatement node) {
		addNode(node, EmptyStatement.class, "visitEmptyStatement");
	}

	public void visitSwitch(SwitchStatement node) {
		addNode(node, SwitchStatement.class, "visitSwitch");
	}

	public void visitCaseStatement(CaseStatement node) {
		addNode(node, CaseStatement.class, "visitCaseStatement");
	}

	public void visitBreakStatement(BreakStatement node) {
		addNode(node, BreakStatement.class, "visitBreakStatement");
	}

	public void visitContinueStatement(ContinueStatement node) {
		addNode(node, ContinueStatement.class, "visitContinueStatement");
	}

	public void visitSynchronizedStatement(SynchronizedStatement node) {
		addNode(node, SynchronizedStatement.class, "visitSynchronizedStatement");
	}

	public void visitThrowStatement(ThrowStatement node) {
		addNode(node, ThrowStatement.class, "visitThrowStatement");
	}

	public void visitMethodCallExpression(MethodCallExpression node) {
		addNode(node, MethodCallExpression.class, "visitMethodCallExpression");
	}

	public void visitStaticMethodCallExpression(StaticMethodCallExpression node) {
		addNode(node, StaticMethodCallExpression.class, "visitStaticMethodCallExpression");
	}

	public void visitConstructorCallExpression(ConstructorCallExpression node) {
		addNode(node, ConstructorCallExpression.class, "visitConstructorCallExpression");
	}

	public void visitBinaryExpression(BinaryExpression node) {
		addNode(node, BinaryExpression.class, "visitBinaryExpression");
	}

	public void visitTernaryExpression(TernaryExpression node) {
		addNode(node, TernaryExpression.class, "visitTernaryExpression");
	}

	public void visitShortTernaryExpression(ElvisOperatorExpression node) {
		addNode(node, ElvisOperatorExpression.class, "visitShortTernaryExpression");
	}

	public void visitPostfixExpression(PostfixExpression node) {
		addNode(node, PostfixExpression.class, "visitPostfixExpression");
	}

	public void visitPrefixExpression(PrefixExpression node) {
		addNode(node, PrefixExpression.class, "visitPrefixExpression");
	}

	public void visitBooleanExpression(BooleanExpression node) {
		addNode(node, BooleanExpression.class, "visitBooleanExpression");
	}

	public void visitNotExpression(NotExpression node) {
		addNode(node, NotExpression.class, "visitNotExpression");
	}

	public void visitClosureExpression(ClosureExpression node) {
		addNode(node, ClosureExpression.class, ""); 
		if(node.getParameters() != null && node.getParameters().length > 0){
			for(Parameter pr : node.getParameters()){
				visitParameter(pr);
			}
			super.visitClosureExpression(node);
		}
	}

	/**
	 * Makes walking parameters look like others in the visitor.
	 */
	public void visitParameter(Parameter node) {
		addNode(node, Parameter.class, "");
		if(node.getInitialExpression() != null && node.hasInitialExpression()){
			node.getInitialExpression().visit(this);
		}		
	}

	public void visitTupleExpression(TupleExpression node) {
		addNode(node, TupleExpression.class, "visitTupleExpression");
	}

	public void visitListExpression(ListExpression node) {
		addNode(node, ListExpression.class, "visitListExpression");
	}

	public void visitArrayExpression(ArrayExpression node) {
		addNode(node, ArrayExpression.class, "visitArrayExpression");
	}

	public void visitMapExpression(MapExpression node) {
		addNode(node, MapExpression.class, "visitMapExpression");
	}

	public void visitMapEntryExpression(MapEntryExpression node) {
		addNode(node, MapEntryExpression.class, "visitMapEntryExpression");
	}

	public void visitRangeExpression(RangeExpression node) {
		addNode(node, RangeExpression.class, "visitRangeExpression");
	}

	public void visitSpreadExpression(SpreadExpression node) {
		addNode(node, SpreadExpression.class, "visitSpreadExpression");
	}

	public void visitSpreadMapExpression(SpreadMapExpression node) {
		addNode(node, SpreadMapExpression.class, "visitSpreadMapExpression");
	}

	public void visitMethodPointerExpression(MethodPointerExpression node) {
		addNode(node, MethodPointerExpression.class, "visitMethodPointerExpression");
	}

	public void visitUnaryMinusExpression(UnaryMinusExpression node) {
		addNode(node, UnaryMinusExpression.class, "visitUnaryMinusExpression");
	}

	public void visitUnaryPlusExpression(UnaryPlusExpression node) {
		addNode(node, UnaryPlusExpression.class, "visitUnaryPlusExpression");
	}

	public void visitBitwiseNegationExpression(BitwiseNegationExpression node) {
		addNode(node, BitwiseNegationExpression.class, "visitBitwiseNegationExpression");
	}

	public void visitCastExpression(CastExpression node) {
		addNode(node, CastExpression.class, "visitCastExpression");
	}

	public void visitConstantExpression(ConstantExpression node) {
		addNode(node, ConstantExpression.class, "visitConstantExpression");
	}

	public void visitClassExpression(ClassExpression node) {
		addNode(node, ClassExpression.class, "visitClassExpression");
	}

	public void visitVariableExpression(VariableExpression node) {
		addNode(node, VariableExpression.class, "");
		if (node.getAccessedVariable() != null) {
			if(node.getAccessedVariable() instanceof Parameter) {
				visitParameter((Parameter)node.getAccessedVariable());
			}
			else if(node.getAccessedVariable() instanceof DynamicVariable) {
				addNode(node.getAccessedVariable(), DynamicVariable.class, "");
				if(node.hasInitialExpression()){
					node.visit(this);
				}
			}
		}
	}

	public void visitDeclarationExpression(DeclarationExpression node) {
		addNode(node, DeclarationExpression.class, "visitDeclarationExpression");
	}

	public void visitPropertyExpression(PropertyExpression node) {
		addNode(node, PropertyExpression.class, "visitPropertyExpression");
	}

	public void visitAttributeExpression(AttributeExpression node) {
		addNode(node, AttributeExpression.class, "visitAttributeExpression");
	}

	public void visitFieldExpression(FieldExpression node) {
		addNode(node, FieldExpression.class, "visitFieldExpression");
	}

	public void visitGStringExpression(GStringExpression node) {
		addNode(node, GStringExpression.class, "visitGStringExpression");
	}

	public void visitCatchStatement(CatchStatement node) {
		addNode(node, CatchStatement.class, "");
		if (node.getVariable() != null) 
			visitParameter(node.getVariable());
		super.visitCatchStatement(node);	
	}

	public void visitArgumentlistExpression(ArgumentListExpression node) {
		addNode(node, ArgumentListExpression.class, "visitArgumentlistExpression");
	}

	public void visitClosureListExpression(ClosureListExpression node) {
		addNode(node, ClosureListExpression.class, "visitClosureListExpression");
	}

	public void visitBytecodeExpression(BytecodeExpression node) {
		addNode(node, BytecodeExpression.class, "visitBytecodeExpression");
	}

	public void visitListOfExpressions(List<? extends Expression> list) {
		for(Expression node : list){
			if (node instanceof NamedArgumentListExpression ) {
				addNode(node, NamedArgumentListExpression.class, "");
				node.visit(this);
			} else {
				node.visit(this);
			}			
		}
	}
}
