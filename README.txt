
## Para hacer andar el emulador en android studio:

	sudo apt-get install lib64stdc++6:i386

	sudo apt-get install mesa-utils

## Buscando la dirreccion sigiente:

	cd YOURPATH/Android/Sdk/emulator/lib64

## Correr dentro de ese directorio estas lineas:

	mv libstdc++/ libstdc++.bak

	ln -s /usr/lib64/libstdc++.so.6  libstdc++

## Finally, run your application again using emulator.
