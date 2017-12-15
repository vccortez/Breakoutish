# Atividade prática de programação

Essa atividade prática consiste em atualizar o projeto Android **Breakoutish** para completar cada passo descrito pelas tarefas apresentadas abaixo.
Cada participante deve completar cada tarefa **duas vezes**, uma vez para cada API que avaliada.
As APIs que serão usadas nessa prática são: A API da biblioteca **Gamepad**; e a API do SDK **padrão** do Android.
Durante a prática, os participantes **podem** utilizar recursos online, como a documentação das respectivas APIs, e dois documentos extra de treinamento: [treinamento-android](treinamento-android.md) e [treinamento-gamepad](treinamento-gamepad.md).
A seguir, serão apresentadas as tarefas a serem realizadas em sequência no projeto **Breakoutish**.
O código inicial do projeto apresenta comentários `// TODO: ...` localizando a posição em que cada passo das tarefas pode começar a ser implementado.

## Tarefa A: MainActivity

O objetivo dessa tarefa é implementar e fazer uso da orientação do dispositivo em relação à direção do Norte magnético para mover a seta de seleção na tela inicial do jogo.
Essa tarefa pode ser realizada em 5 passos principais:

1. Implementar a **interface** adequada para receber eventos na classe `MenuView`;
2. Inicializar e configurar os objetos necessários na classe `MainActivity` para que a `MenuView` possa receber os valores da orientação do dispositivo;
3. Implementar os métodos `onResume()` e `onPause()` da `MainActivity` para iniciar e cancelar o recebimento de eventos, respectivamente;
4. Implementar no método **callback** da classe `MenuView` os mecanismos necessários para obter a orientação do dispositivo e usar o primeiro valor (**azimuth**) como argumento para a chamada do método `atualizarEixo()`;
5. Executar a aplicação e verificar se a seta se move quando o dispositivo for rotacionado.

## Tarefa B: BreakoutActivity e BreakoutView

O objetivo dessa tarefa é implementar e fazer uso da orientação do dispositivo em relação à direção do Norte magnético para mover o rebatedor do jogador na tela principal do jogo.
Essa tarefa pode ser realizada em 5 passos principais:

1. Implementar a **interface** adequada para receber eventos na classe `BreakoutView`;
2. Inicializar e configurar os objetos necessários na classe `BreakoutActivity` para que a `BreakoutView` possa receber os valores da orientação do dispositivo;
3. Implementar os métodos `onResume()` e `onPause()` da `BreakoutActivity` para iniciar e cancelar o recebimento de eventos, respectivamente;
4. Implementar no método **callback** da classe `BreakoutView` os mecanismos necessários para obter a orientação do dispositivo e usar o primeiro valor (**azimuth**) como argumento para a chamada do método `atualizarEixo()`;
5. Executar a aplicação e verificar se o rebatedor se move de forma esperada quando o dispositivo for rotacionado.
