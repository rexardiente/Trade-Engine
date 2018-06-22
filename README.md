# TRADE ENGINE 

```
createuser mr-trade-engine
createdb --encoding=UTF8 --owner=mr-trade-engine mr-trade-engine
```

## Get Order Book Request

```javascript
{
  "command": "order_book",
  "id_account_ref": "dc59a3e6-c2e8-49d6-9ee3-5e39ad5264d5",
  "currency_base": "BTC",
  "currency_counter": "JPY",
  "levels": 1 // optional value
}
```

For Understanding transaction results
```
          create     fill / partial
buy       -counter   +base
sell      -base      +counter
cancel    nothing    nothing

cancelled buy   +counter
cancelled sell  +base
```

BTC/JPY Example:
```
        create    fill / partial
buy     -JPY      +BTC
sell    -BTC      +JPY
cancel  nothing   nothing

cancelled buy   +JPY
cancelled sell  +BTC
```