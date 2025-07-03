
# 🃏 BlackJack Retro v1.0.0

Un juego de BlackJack clásico y básico para terminal con interfaz ASCII art retro.

## 🚀 Descarga y Ejecución Rápida

1. **Descarga** el archivo `blackjack-retro.jar` desde esta release.
2. **Ejecuta** en tu terminal:

   ```bash
   java -jar blackjack-retro.jar
   ```

## 📋 Requisitos

- Java 17 o superior instalado en tu sistema.
- Terminal compatible con caracteres Unicode (para los símbolos de cartas).

### ✅ Verificar instalación de Java

```bash
java -version
```

### 📦 Si no tienes Java instalado:

- **Windows**: Descarga desde [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) o instala OpenJDK.
- **Linux**:
  ```bash
  sudo apt install openjdk-17-jre       # Ubuntu/Debian  
  sudo yum install java-17-openjdk      # CentOS/RHEL
  ```
- **macOS**:
  ```bash
  brew install openjdk@17
  ```

## 🎮 Cómo Jugar

**🎯 Objetivo**: Conseguir 21 puntos o acercarse lo más posible sin pasarse.

### 🃠 Valores de cartas:

- As = 11 (o 1 si te conviene más).
- Figuras (J, Q, K) = 10.
- Números = su valor facial.

### 🔁 Mecánica:

- Recibes 2 cartas iniciales.
- El dealer también recibe 2 cartas (una oculta).
- Decides si pedir más cartas (`s`) o plantarte (`n`).
- El dealer debe pedir carta si tiene menos de 17.
- Gana quien esté más cerca de 21 sin pasarse.

## 🎯 Características

- ✨ Interfaz ASCII art colorida.
- 🃏 Representación visual de cartas con colores diferenciados.
- 🎲 Mazo que se baraja automáticamente.
- 🔄 Partidas ilimitadas.
- 🎨 Colores rojos para corazones y diamantes, negros para picas y tréboles.
- 🖥️ Compatible con Windows, Linux y macOS.

## 📸 Vista Previa

```
╔══════════════════════════════════════════╗
║             BLACKJACK RETRO              ║
║             César Castillo               ║
╚══════════════════════════════════════════╝

Dealer: 
┌─────────┐ ┌─────────┐
│ K       │ │## XX ## │
│         │ │         │
│    ♠    │ │    ?    │
│         │ │         │
│      K  │ │## XX ## │
└─────────┘ └─────────┘

Jugador: (Puntos: 19)
┌─────────┐ ┌─────────┐ 
│ 9       │ │ A       │ 
│         │ │         │ 
│    ♣    │ │    ♥    │ 
│         │ │         │ 
│      9  │ │      A  │ 
└─────────┘ └─────────┘
```

## 🛠️ Solución de Problemas

- ❌ **Símbolos no se ven correctamente**: Asegúrate de que tu terminal soporte UTF-8.
- 🎨 **Colores no aparecen**: Algunos terminales antiguos pueden no soportar colores ANSI.
- 🚫 **Java no encontrado**: Verifica que Java esté en tu PATH.

---

## 🤝 Contribuciones

¡Las mejoras son bienvenidas!  
Siéntete libre de abrir *issues* o *pull requests* en el repositorio.
