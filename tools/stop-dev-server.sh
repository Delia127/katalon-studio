#!/usr/bin/env bash
lsof -ti:33333 | xargs kill
lsof -ti:9999 | xargs kill

