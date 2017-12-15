# Gamepad

A biblioteca *Gamepad* proporciona uma API alternativa para a definição de fontes de dados contextuais em jogos cientes do contexto, como os sensores físicos e lógicos, provedores de conteúdo, serviços Web, entre outros.
A ideia principal se baseia no relacionamento entre o controle de *videogame* e o jogo, no qual o controle, como dispositivo de entrada principal, recebe e interpreta as interações do jogador na forma de eventos. Os eventos, por sua vez, indicam o estado do controle em um determinado momento do jogo, mas cabe ao desenvolvedor interpretar os diferentes eventos e determinar quais serão os seus efeitos no estado do jogo.

Com base nessa abstração, a biblioteca *Gamepad* utiliza 4 entidades para simular esse relacionamento: o `Gamepad`, representando o controle; o `Input`, representando botões; o `Source`, representando as interações do jogador; e o `Handler`, que representa a interpretação das interações.

Uma visão geral de como essas entidades estão interligadas é ilustrada no exemplo a seguir:

```kotlin
// Supondo-se que a variável `gamepad` possui uma instância de
// `Gamepad`, essa instância é apenas um container vazio que
// precisa de botões para funcionar.
var gamepad: Gamepad = /** instância de `Gamepad` **/

// `observadorLuz` é um botão que envia eventos para o `gamepad`
// sempre que a luminosidade passar de um certo nível. Objetos do
// tipo `Input` precisam de dois fatores para que possam exercer
// essa função: fontes de 'sinais' e um handler.
var observadorLuz: PressingButton = /** subclasse de `Input` **/

// `luz` é uma fonte de sinais que envia periodicamente observações
// sobre a luminosidade. Essa fonte será ativada pelo `gamepad`
// quando ela estiver associada a um botão.
var luz: Luminosity = /** subclasse de `Source` **/

// `temLuzAlta` é a função que interpreta os dados da fonte `luz` e
// informa ao botão `observadorLuz`. Essa função compõe o botão 
// `observadorLuz` e representa uma interpretação específica para
// uma ou mais fontes de sinais associadas ao botão.
var temLuzAlta: Handler = /** lambda `(Entrada) -> Resposta` **/
```

### Criando uma instância de `Gamepad`

Como mencionado anteriormente, o objeto `Gamepad` pode ser comparado a um controle de *videogame* (um *joystick*). De maneira semelhante a um *joystick*, a função de um `Gamepad` é agrupar um conjunto de **botões** relacionados que podem ser usados pelo jogador para interagir com o jogo. Sempre que o jogador pressiona um botão do *joystick*, o efeito dessa ação no estado do jogo dependerá de como as regras do jogo foram implementadas, ou seja, o jogo é apenas notificado no **evento** que um botão do *joystick* for pressionado.

Criar um instância de `Gamepad` é o ponto de partida para transformar um jogo comum em um jogo ciente do contexto. Para que seja possível instanciar um `Gamepad`, é necessário implementar a interface `ViewHolder`.
O papel da interface `ViewHolder` é semelhante ao papel da interface `SensorEventListener`, no sentido de que ambas as interfaces requerem a implementação de um método *callback* para o qual serão enviados eventos.

```kotlin
// O construtor de `Gamepad` requer um objeto que implemente `ViewHolder`.
class Gamepad(val viewHolder: ViewHolder)
```

Um `ViewHolder` possui uma propriedade chamada `instance` do tipo `View`, e um método *callback* chamado `onGamepadEvent()` que recebe os eventos de todos os botões associados ao `Gamepad`.
Em **Kotlin**, é possível criar objetos *singleton* com a palavra-chave `object`. No exemplo abaixo, um objeto *singleton* é usado para implementar a interface `ViewHolder`.

```kotlin
// Encontra a `View` principal da `Activity` atual
val mainView = findViewById(android.R.id.content)

// Uma instância de `ViewHolder` é implementada
// com a ajuda de um objeto singleton e armazenada
// na variável `viewHolder`.
val viewHolder = object : ViewHolder {
  // A propriedade `instance` é inicializada com a
  // `View` principal encontrada acima.
  override val instance: View = mainView

  // Imprime cada evento recebidos
  override fun onGamepadEvent(event: GamepadEvent) = print(event)
}
```

Finalmente, tendo em mãos um objeto que implemente a interface `ViewHolder`, é possível instanciar um objeto `Gamepad` por meio do estilo de [construção type-safe](https://kotlinlang.org/docs/reference/type-safe-builders.html) da linguagem Kotlin, que permite inicializar objetos utilizando funções com recebedor (*receiver*), como no trecho a seguir:

```kotlin
// Cria uma instância de `Gamepad` e armazena na variável `gamepad`.
// Aqui, `viewHolder` é um objeto que implementa a interface `ViewHolder`.
val gamepad: Gamepad = viewHolder.gamepad { /** Botões serão adicionados aqui **/ }
```

Depois que uma instância de `Gamepad` for inicializada, é necessário ativar os seus botões para receber eventos com o método `enableInputEvents()`, ou desativar os botões quando não forem mais necessários com o método `disableInputEvents()`. Essa tarefa será normalmente realizada nos eventos de ciclo de vida de uma `Activity`, nomeadamente nos métodos `onResume()` e `onPause()`, por exemplo:

```kotlin
class ExemploActivity : AppCompatActivity() {
  // Quando a `Activity` for (re)iniciada,
  // habilite os botões para receber eventos.
  override fun onResume() {
    super.onResume()

    gamepad.enableInputEvents()
  }

  // Quando a `Activity` for pausada, desabilite
  // os botões para não receber eventos.
  override fun onPause() {
    super.onPause()

    gamepad.disableInputEvents()
  }
}
```

### Adicionando botões (`Input`) ao `Gamepad`

Os botões de um `Gamepad` são subclasses do tipo `Input`. Para que um botão seja ativado em um `Gamepad`, é necessário fornecer dois parâmetros: uma ou mais fontes de sinais (suclasses de `Source`); e uma função *handler* que irá interpretar os sinais.

Existem diferentes tipos (subclasses) de botões oferecidos pela API. A principal diferença entre cada tipo é o tipo de evento que o botão retorna, e o tipo de dado de resposta que um botão espera de seu *handler*.
No trecho a seguir, um botão do tipo `AxisButton` sem é adicionado a um `Gamepad`:

```kotlin
// Declara um `Gamepad` a partir de um `ViewHolder`.
val gamepad = viewHolder.gamepad {
  // Adiciona um botão do tipo `AxisButton`
  // no `gamepad` com a função `axisButton()`.
  axisButton()
}
```

Um botão `AxisButton` pode retornar eventos com **um** valor numérico do tipo `Float`. Por esse motivo, qualquer *handler* oferecido para um botão desse tipo precisa retornar um valor `Float`.
No trecho abaixo, um botão `AxisButton` é inicializado com uma fonte de sinais do acelerômetro e um *handler* que calcula a soma da aceleração de todos os eixos do acelerômetro:

```kotlin
// Declara um `Gamepad` a partir de um `ViewHolder`.
val gamepad = viewHolder.gamepad {
  // Adiciona um botão do tipo `AxisButton`
  // no `gamepad` com a função `axisButton()`.
  axisButton {
    // Adiciona uma fonte do tipo `Accelerometer`
    // nesse botão com a função `accelerometer()`
    // e salva a fonte na variável `acc`.
    val acc = accelerometer("acelerometro")

    // Define uma função handler que recebe um
    // `ContextMap` e retorna um valor `Float`.
    handler = fun(mapa: ContextMap): Float {
      // Recupera os dados do acelerômetro dentro
      // de `mapa` por meio da variável `acc`.
      val aceleracao = mapa[acc]!!

      // Retorna um `Float`, somando as acelerações
      // de todos os eixos do acelerômetro.
      return (aceleracao.x + aceleracao.y + aceleracao.z)
    }
  }
}
```

A assinatura de um *handler* sempre reflete o tipo de retorno que o seu botão espera.
Além disso, os valores que um *handler* recebe como parâmetro são do tipo `ContextMap`, representando um mapa que armazena os dados de todas as fontes associadas àquele botão.

```kotlin
// `TIPO` deve respeitar o tipo que
// uma subclasse de botão espera.
handler = fun(mapa: ContextMap): TIPO {
  // Faça uso das informações de sensores em `mapa`
  // passando uma referência do sensor. Por exemplo:
  val valores = mapa[sensorExemplo]!!

  // E retorne um valor do tipo `TIPO`
  return valores.tipoEsperado
}
```

Outra subclassese de `Input` é a `PressingButton`, que representa um botão simples cujo estado interno pode ser **pressionado** ou **solto**. Dessa forma, um `PressingButton` faz uso de um valor *booleano* para determinar se ele está sendo pressionado (quando **verdadeiro**), e somente quando esse valor voltar a ser **falso** o `PressingButton` enviará um evento indicando que o botão foi pressionado.
O exemplo a seguir ilustra como adicionar esse elemento em um `Gamepad`:

```kotlin
val gamepad = viewHolder.gamepad {
  // Adiciona uma instância vazia do
  // `PressingButton` ao `gamepad`.
  pressingButton()

  // Adiciona uma nova instância do
  // `PressingButton` com um sensor
  // de orientação (`Orientation`).
  pressingButton {
    val orientacao = orientation("orientacao")

    handler = fun(mapa): Boolean {
      val dados = mapa[orientacao]!!
      // Esse botão será pressionado somente se
      // o dispositivo tiver apontado na direção
      // de 30 graus em torno do norte magnético.
      return abs(dados.azimuth) < 0.524f
    }
  }
}
```
