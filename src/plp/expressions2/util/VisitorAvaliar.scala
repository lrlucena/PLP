package plp.expressions2.util

import plp.expressions1.expression.Valor
import plp.expressions2.expression.ExpDeclaracao
import plp.expressions2.expression.Id
import plp.expressions2.memory.AmbienteExecucao
import plp.expressions1.util.{VisitorAvaliar=>VisitorLE1}

class VisitorAvaliar(ambiente: AmbienteExecucao)
  extends VisitorLE1() with Visitor[Valor] {
  override def visit(declaracao: ExpDeclaracao): Valor = {
    ambiente.incrementa
    val resolvedValues = resolveValueBindings(declaracao)
    includeValueBindings(ambiente, resolvedValues)
    declaracao.expressao.accept(this)
    val result = declaracao.expressao.accept(this).asInstanceOf[Valor]
    ambiente.restaura
    result
  }

  private def includeValueBindings(ambiente: AmbienteExecucao, resolvedValues: Map[Id, Valor]) {
    for (id <- resolvedValues.keySet) {
      val valor = resolvedValues.get(id).get
      ambiente.map(id, valor)
    }
  }

  private def resolveValueBindings(expressaoDec: ExpDeclaracao): Map[Id, Valor] = {
    var resolvedValues = Map[Id, Valor]()
    for (declaration <- expressaoDec.seqdecVariavel) {
      val valor = declaration.expressao.accept(this)
      resolvedValues = resolvedValues + (declaration.id -> valor)
    }
    resolvedValues
  }

  override def visit(id: Id) = {
    println (ambiente.get(id))
    ambiente.get(id)
  }
}