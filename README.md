# picasso

picasso is a headless scribbling tool forked from [Alchemy](http://al.chemy.org/).

```
$ cat << EOF > instructions
CREATE 7
DRAW 85 500 180 100 0 30 2
DRAW 180 100 280 480 0 30 2
DRAW 280 480 520 90 0 30 2
DRAW 520 90 650 490 0 30 2
DRAW 650 490 900 100 0 30 2
CREATE 3
THICKNESS 8
DRAW 85 500 180 100 0 30 2
CREATE 6
FILL
DRAW 100 300 900 300 0 300 1
EOF
$ ./Alchemy instructions output.svg
```

... which produces:

![demo](https://cloud.githubusercontent.com/assets/88302/5993935/69b29a3a-aab5-11e4-85c3-7522368b78bd.png)

Disclaimer: work in progress. Not all features supported yet.
