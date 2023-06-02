#!/bin/bash
# kill any existing run_netjumper.sh processes
pkill -f run_netjumper.sh
#run the script
cd /etc/NetJumperDaemon/NetJumperver1.0 && ./run_netjumper.sh
