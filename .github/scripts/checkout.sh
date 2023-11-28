#!/bin/bash

if [ $# -ne 1 ]; then
    echo "The checkout.sh script requires exactly 1 argument"
    exit 1
fi

branch=$1

if [ "$(git ls-remote --heads origin refs/heads/$branch | wc -l)" -eq "1" ]; then
    git fetch origin
    git checkout -t origin/$branch
    echo "branch_existed=true" >> $GITHUB_OUTPUT
else
    git checkout main
    git checkout -b $branch
    echo "branch_existed=false" >> $GITHUB_OUTPUT
fi
