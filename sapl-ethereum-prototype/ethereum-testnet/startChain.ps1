geth --datadir ./ptn init genesis.json
geth --nodiscover --datadir ./ptn --networkid 2000 --rpc --mine console
rm -r ./ptn/geth/*
rm ./ptn/history