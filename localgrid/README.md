#To start the grid

##On Windows

```
cd windows/
startGrid
```

##On Linux

###Before running the local grid:

Download appropriate chromedriver from
[here](http://chromedriver.chromium.org/downloads) into:
- */chrome/

Download appropriate geckodriver from
[here](https://github.com/mozilla/geckodriver/releases) into:
- */firefox/

You can also modify the config file of each node to fit your requirements.

###Make all scripts executable:
```
cd linux/
chmod +x chrome/startNode.sh
chmod +x firefox/startNode.sh
chmod +x hub/startHub.sh
chmod +x startGrid.sh
```

To start the grid:
```
cd linux/
./startGrid.sh
```



## To update the browser versions

Edit the respective `startNode.sh` files, for example `linux/chrome/startNode.sh`:

```
java -Dwebdriver.chrome.driver=./chromedriver -jar ../../selenium.jar -role node -maxSession 10 -port 6000 -host $ip -hub http://$ip:4444/grid/register -browser browserName=chrome,**version=70**,platform=LINUX,maxInstances=5 --debug
```