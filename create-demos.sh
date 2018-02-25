#!/bin/bash

printf "\nCleaning\n"
lein clean

printf "\nGenerating basics demo\n"
lein cljsbuild once basics-min

printf "\nGenerating examples demo\n"
lein cljsbuild once examples-min
