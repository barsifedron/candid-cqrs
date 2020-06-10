# happy-neighbourhood

A (more opiniated) cqrs domain example

## Goals :
1. Illustrate real command buses usages
2. I went an extra mile to prove that all this code can exist outside of any Sping (or other framework) pacakge. 
In practice I might not be that extreme in a real project. But i would limit it to adding __@Controller__ annotation on top of classes.

## Disclaimer :

This is obviously a small illustrating project but it does already quite a few things. If you feel like something would be worth illustrating here, please let me know.


## The app:

The __Happy neighbouhood (TM)__ association allows you to borrow tools, games and various items for a bargain.
People love it so much that we now need to provide an app to keep track of all the loans.

Here are the specs (work in progress):

- New members can be registered - DONE
- New items can be registered - DOME
- A member can borrow an item - DONE
- A daily (small) fee applies for a borrowed item. It can be different for each item - DONE
- A daily extra fine is applied _on top_ of the daily fee when a member is late in returning an item - DONE
- When a member borrows an item, a confirmation email with basic information is sent to the member
- When a member returns an item, an email is sent to her with the details of the amount that will be charged on her account. It should contain the daily details of the cost calculation. - DONE
- When a member is late, (reminders emails should be sent to the member - TODO
- ... 


## Things to look at :

- Check how, on the command side, we focus on the minimum effective number of methods. 
Repositories are a good example. While the collusion of write and read side queries usually bloats them, here they are pretty small.
- The read side (queries)... The only rule of the read side is that there is no rule on the read side. Any dirty trick is ok : direct sql, hibernate projections... I have a soft spot for query dsl but that is just me.
- Side effects with domain events. Try to add yours. This architecture makes it really simple to add/ remove side effect behaviour with an almost non existant effect on your command handlers (where the core of your domain and logic lives))
- Closure of operations when calculating the bill. Everybody should use such ninja tricks as they can eradicate annoying complexities in your code. A really nice article on that can be found here : https://www.arolla.fr/blog/wp-content/uploads/2018/10/DomainModelingwithMonoids.pdf

