## Investigation result

### TABLE students
#### insert 100K of users
#### index on column 'name'/'surname'/'phone_number'

|  |Without index| B-tree | Hash | GIN | GIST |
|:---:|:---:|:---:|:---:|:---:|:---:|
|index size before data insertion (copying)|-|16 kB|40 kB|24 kB|16 kB|
|index size after data insertion (copying)|-|2904 kB|7256 kB| 2616 kB|5424 kB|
|data insertion time (copying)|687 ms|1 s 225 ms|962 ms|4 s 794 ms|4 s 490 ms|
|index creation time before data inserting (copying)|-|10 ms|16 ms|24 ms|14 ms|
|index creation time after data inserting (copying)|-|291 ms|257 ms|387 ms|3 s 116 ms|
|find user by name (exact match)|148 rows retrieved starting from 1 in 119 ms (execution: 46 ms, fetching: 73 ms)|148 rows retrieved starting from 1 in 81 ms (execution: 19 ms, fetching: 62 ms)|148 rows retrieved starting from 1 in 64 ms (execution: 11 ms, fetching: 53 ms)|-|-|
|find user by surname (partial match)|129 rows retrieved starting from 1 in 107 ms (execution: 37 ms, fetching: 70 ms)|-|-|129 rows retrieved starting from 1 in 79 ms (execution: 16 ms, fetching: 63 ms)|129 rows retrieved starting from 1 in 70 ms (execution: 14 ms, fetching: 56 ms)|
|find user by phone number (partial match)|129 rows retrieved starting from 1 in 136 ms (execution: 48 ms, fetching: 88 ms)|-|-|129 rows retrieved starting from 1 in 57 ms (execution: 20 ms, fetching: 37 ms)|129 rows retrieved starting from 1 in 75 ms (execution: 41 ms, fetching: 34 ms)|
|find user with marks by user surname (partial match)|500 rows retrieved starting from 1 in 143 ms (execution: 117 ms, fetching: 26 ms)|-|-|500 rows retrieved starting from 1 in 111 ms (execution: 73 ms, fetching: 38 ms)|500 rows retrieved starting from 1 in 159 ms (execution: 131 ms, fetching: 28 ms)|


### TABLE subjects
#### insert 1K of subjects
#### index on column 'subject_name'

|  |Without index| B-tree | Hash | GIN | GIST |
|:---:|:---:|:---:|:---:|:---:|:---:|
|index size before data insertion (copying)|-|16 kB|56 kB|24 kB|16 kB|
|index size after data insertion (copying)|-|56 kB|88 kB|56 kB|88 kB|
|data insertion time (copying)|64 ms|48 ms|60 ms|60 ms|86 ms|
|index creation time before data inserting (copying)|-|16 ms|14 ms|20 ms|17 ms|
|index creation time after data inserting (copying)|-|27 ms|22 ms|35 ms|55 ms|


### TABLE exam_results 
#### insert 1M of marks
#### index on column 'mark'

|  |Without index| B-tree | Hash | GIN | GIST |
|:---:|:---:|:---:|:---:|:---:|:---:|
|index size before data insertion|-|16 kB|56 kB|24 kB|16 kB|
|index size after data insertion|-|7 MB|47 MB|3 MB|58 MB|
|data insertion time|31 s 385 ms|32 s 540 ms|58 s 589 ms|33 s 739 ms|39 s 72 ms|
|index creation time before data inserting|-|15 ms|18 ms|13 ms|15 ms|
|index creation time after data inserting|-|947 ms|8 s 546 ms|553 ms|6 s 825 ms|
