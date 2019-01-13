package me.zhanglun.ahocorasick

import scala.collection.mutable

class Emit(val start: Int, val end: Int, val keyword: String)

class State(val depth: Int = 0) {
  val rootState: State = if (depth == 0) null else this
  val success: mutable.HashMap[Char, State] = new mutable.HashMap[Char, State]()
  var failure: State = null
  var emit: Option[String] = None

  def addState(keyword: String): State = {
    var state = this
    keyword foreach (x => {
      state = state.addState(x)
    })
    state
  }

  def addState(char: Char): State = {
    val state = this.success get char
    state match {
      case Some(s) => s
      case None =>
        val s = new State(this.depth + 1)
        this.success(char) = s
        s
    }
  }

  def nextState(char: Char, returnNull: Boolean = false): State = {
    val state = this.success get char
    state match {
      case Some(s) => s
      case None if depth != 0 => if (returnNull) null else this.rootState
      case None if depth == 0 => this
    }
  }

  def addEmit(emit: String): Unit = {
    this.emit match {
      case None if emit.length > 0 => this.emit = Some(emit)
      case _ =>
    }
  }


}

class Trie(val depth: Int = 0) {

  trie =>

  class TrieBuilder {
    def addKeyword(keyword: String): TrieBuilder = {
      trie.addKeyword(keyword)
      this
    }

    def addKeywords(keywords: String*): TrieBuilder = {
      keywords.foreach(addKeyword)
      this
    }

    def addKeywords(keywords: Iterable[String]): TrieBuilder = {
      keywords foreach addKeyword
      this
    }

    def build(): Trie = {
      trie.constructFailureStates()
      trie
    }
  }


  private def addKeyword(keyword: String): Unit = {
    rootState.addState(keyword).addEmit(keyword)
  }


  private def constructFailureStates(): Unit = {
    val queue = new mutable.Queue[State]
    var startState = this.rootState
    for (state <- startState.success.values) {
      state.failure = startState
      queue.enqueue(state)
    }
    while (queue.nonEmpty) {
      val currentState = queue.dequeue()
      for (transition <- currentState.success.keySet) {
        var targetState = currentState.nextState(transition)
        queue.enqueue(targetState)

        var traceFailureState = currentState.failure
        while (traceFailureState.nextState(transition) == null) traceFailureState = traceFailureState.failure
        targetState.failure = traceFailureState.nextState(transition)
      }
    }
  }

  def searchOne(text: String): Iterable[Emit] = return search(text, true)

  def search(text: String, onlyOne: Boolean = false): Iterable[Emit] = {
    var currentState = this.rootState
    val res = new mutable.ListBuffer[Emit]

    for (position <- 0 until text.length) {
      val char = text(position)
      var notSet = true
      var newCurrentState = currentState.nextState(char, true)

      def storeEmit(currentState: State): Boolean = {
        currentState.emit match {
          case Some(str) if notSet =>
            notSet = false
            res.append(new Emit(position - str.length + 1, position, str))
            true
          case _ => false
        }
      }

      while (newCurrentState == null) {
        // before match
        if (storeEmit(currentState) && onlyOne)
          return res

        currentState = currentState.failure

        // first fail try add emit
        if (storeEmit(currentState) && onlyOne)
          return res

        newCurrentState = currentState.nextState(char, true)


      }
      currentState = newCurrentState
      if (position == text.length - 1)
        currentState match {
          case s: State if notSet =>
            s.emit match {
              case Some(str) =>
                res.append(new Emit(position - str.length + 1, position, str))
              case _ =>

            }
          case _ =>
        }


    }
    res
  }

  val rootState: State = new State

  private val _builder = new TrieBuilder()

  def build(): TrieBuilder = _builder


}
