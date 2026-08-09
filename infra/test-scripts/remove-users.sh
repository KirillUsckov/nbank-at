#!/bin/bash

API_URL="http://localhost:4111/api/v1"
AUTH="admin:admin"

curl -s -u "$AUTH" "$API_URL/admin/users" |
jq -c '.[] | select(.role != "ADMIN")' |
while read -r USER; do
    ID=$(echo "$USER" | jq -r '.id')
    USERNAME=$(echo "$USER" | jq -r '.username')

    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
        -u "$AUTH" \
        -X DELETE \
        "$API_URL/admin/users/$ID")

    if [[ "$HTTP_CODE" =~ ^20[04]$ ]]; then
        echo "✅ Deleted $USERNAME (id=$ID)"
    else
        echo "❌ Failed to delete $USERNAME (id=$ID), HTTP $HTTP_CODE"
    fi
done