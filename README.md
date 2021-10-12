0: make sure lein is installed:

https://github.com/technomancy/leiningen

$ sudo apt-get install leiningen

clone this git repository

$ cd http-weather

$ lein ring server

a browser window should open to localhost:3000

wou will be prompted to navigate to:

localhost:3000/{api-key}/{latitude}/{longitude}

alternatively, you can curl:

$ curl 127.0.0.1:3000/{api-key}/{latitude}/{longitude}

