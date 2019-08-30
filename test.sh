#!/bin/bash

set -xe

npm config delete prefix
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && chmod +x "$NVM_DIR/nvm.sh" && \. "$NVM_DIR/nvm.sh"
[ -s "$NVM_DIR/bash_completion" ] && chmod +x "$NVM_DIR/bash_completion" && \. "$NVM_DIR/bash_completion"