# API Android para acessar sensores

Para monitorar os sensores de um dispositivo Android, primeiro é necessário implementar a interface `SensorEventListener` que possui dois métodos: `onSensorChanged()` e `onAccuracyChanged()`.
Esses métodos receberão eventos sempre que novos dados forem lidos e a precisão dos sensores for alterada, respectivamente.

Após implementar um `SensorEventListener`, é necessário registrá-lo para que ele comece a receber eventos.
O método para registrar, chamado de `registerListener()`, faz parte da classe `SensorManager`, e necessita de um `Sensor` e um valor indicando a velocidade das medições como parâmetros.

O exemplo a seguir demonstra uma `Activity` que implementa a interface `SensorEventListener` para escutar valores do sensor de luminosidade:
```kotlin
class SensorActivity : AppCompatActivity(), SensorEventListener {
  // Declare um SensorManager para administrar os sensores
  lateinit var sensorManager: SensorManager
  // Declare um Sensor para guardar o sensor desejado
  lateinit var sensorLight: Sensor

  // No método onCreate, o manager e o sensor são inicializados
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sensorManager = getSystemService(Context.SENSOR_SERVICE)

    sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
  }

  // No método onResume, o SensorEventListener é registrado
  override fun onResume() {
    super.onResume()

    // A partir desse momento, é possível receber eventos
    sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL)
  }

  // No método onPause, o SensorEventListener é removido
  override fun onPause() {
    super.onPause()

    // A partir desse momento, não é possível receber eventos
    sensorManager.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent) {
    // O sensor de luminosidade retorna um único valor
    // porém muitos dos outros sensores retornam 3
    val luminosidade = event.values[0]
    // Faça algum uso com o valor de luminosidade
  }

  override fun onAccuracyChanged(sensor: Sensor, precisao: Int) {
    // Faça alguma coisa com o novo valor de precisão
  }
}
```

Existem diferentes tipos e quantidades de sensores dependendo do dispositivo.
Porém, os sensores **acelerômetro** (`TYPE_ACCELEROMETER`) e **magnetômetro** (`TYPE_MAGNETIC_FIELD`) são comuns em todos os dispositivos.
Ambos os sensores retornam 3 valores, correspondendo aos eixos X, Y e Z. O sensor **acelerômetro** mede a aceleração aplicada ao dispositivo (m/s*s). Já o sensor **magnetômetro** mede os campos magnéticos do ambiente.

É possível combinar os dados medidos pelos dois sensores para obter a **orientação** do dispositivo móvel, por meio dos métodos `SensorManager.getOrientation()` e `SensorManager.getRotationMatrix()` da seguinte forma:

```kotlin
class OrientationActivity : AppCompatActivity(), SensorEventListener {
  // Declare um SensorManager para administrar os sensores
  lateinit var sensorManager: SensorManager
  // Declare um Sensor para o acelerômetro
  lateinit var sensorAcc: Sensor
  // Declare um Sensor para o magnetômetro
  lateinit var sensorMag: Sensor

  // Declare dois vetores para guardar os valores dos sensores
  var dadosAcc: FloatArray? = null
  var dadosMag: FloatArray? = null

  // No método onCreate, o manager e o sensor são inicializados
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sensorManager = getSystemService(Context.SENSOR_SERVICE)

    sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
  }

  // No método onResume, o SensorEventListener é registrado
  override fun onResume() {
    super.onResume()

    // A partir desse momento, é possível receber eventos
    sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_GAME)

    sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_GAME)
  }

  // No método onPause, o SensorEventListener é removido
  override fun onPause() {
    super.onPause()

    // A partir desse momento, não é possível receber eventos
    sensorManager.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent) {
    // Descobre qual sensor enviou dados e
    // salva os dados no vetor correto
    when (event.sensor.type) {
      Sensor.TYPE_ACCELEROMETER -> dadosAcc = event.values
      Sensor.TYPE_MAGNETIC_FIELD -> dadosMag = event.values
    }

    // Se os dois vetores possuírem dados
    if (dadosAcc != null && dadosMag != null) {
      val matrizIdentidade = FloatArray(9)
      val matrizRotacional = FloatArray(9)

      // Calcula a matriz rotacional do dispositivo
      // a partir dos dados dos sensores
      if (SensorManager.getRotationMatrix(matrizRotacional, matrizIdentidade, dadosAcc, dadosMag)) {
        val orientacao = FloatArray(3)

        // Calcula o vetor de orientação do dispositivo
        // em ângulos de Euler
        SensorManager.getOrientation(matrizRotacional, orientacao)

        // O primeiro valor do vetor de orientação
        // indica o 'azimuth', a direção do Norte magnético
        val direcaoNorte = orientacao[0]
        // Faça alguma coisa com o valor da direção Norte
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor, precisao: Int) {
    // Faça alguma coisa com o novo valor de precisão
  }
}

```

