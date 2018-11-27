# Happy neighborhood


## The app

The Happy Neighborhood association has an inventory of tools, books, games and objects that members can borrow for a few days.
Need a hammer to fix your fence or a board game for one evening? just come pick it up!
Members love it and new ones have been registering on a daily basis. So much in fact, that the association is struggling to keep track of all the flux.
They asked us to build an application with the following specifications :

### DONE
. We can register new adult members.
. We can track when a member borrows and returns a borrowed item
. We can add items to the inventory.
. We can archive items out of the repository. They can not be borrowed anymore.

### TODO
. We can display a list of all items with their availability
. Each item can be borrowed for a max period of time specific to this item. (default is 3 days, not including sundays). No item can ever be borrowed more than 10 days, not including sundays.
. Members can only borrow one item at a time.
. If a member is late in returning an item, a reminder email will be sent
. If a member is more than 3 days late in returning an item, then after returning the item, they can't borrow a new item for 14 days. An email inform them of this fact.
. If an item is not available, members can be registered to a waiting list. They must inform of the number of days they intend to keep the item once it is their turn to have it. They receive an estimated date of availability based on the current waiting list.
. When an item becomes available , the next person in the waiting list receives an email.
. When an item is returned, if delays occurred in the return of the item, an email with the new expected availability of the item will be send to all respective members of the waiting list.
. If more than 10 members are on the waiting list for an item, an email will be send to the association board so they can consider buying a duplicate for this item.
. Each sunday, a report with the status of all the inventory is sent to the association board.
. On the last sunday of the month, a report of all the month items activity is sent to the board,
