#!/bin/bash
echo "limpiando los datos del analyzer"
rm analyzer2/db/*
echo "ok"

echo "limpiando los datos del storage"
rm storage/db/*
rm storage/idx/*
echo "ok"

echo "Limpiando los archivos de eventos de los usuarios"
rm ClientConsole/log/*
echo "ok"
