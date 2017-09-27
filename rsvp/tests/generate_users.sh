#!/bin/bash

users_amount=$1

for((i=0; i < $users_amount; i++)) {
	echo "name_${users_amount}_$i,lastname_${users_amount}_$i,email_${users_amount}_$i@mail.com,company_${users_amount}_$i" >> users_${users_amount}.csv
}
