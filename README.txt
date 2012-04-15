Tarantula
=========

Will read through a given db and create entities on that db.

These entities are also each saveable in its own right.

The save method fills in empty not-nullable properties, creates dependent objects and saves them all.

To make this point a another db, modify src/main/resources/hibernate.cfg.xml

Currently all the test objects are a part of our db, so those tests won't compile.