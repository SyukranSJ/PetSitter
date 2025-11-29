# CareTail: A Transparent Mobile App for Verified Pet Care and Daily Activity Tracking

UG Year 3 COMP3040  
Requirements and Analysis Coursework 1

Group F:  
Thaqif Syahmi 20607625  
Syukran Shabaruddin 20512078  
Viishal Avinash 20617818

Module Convener : Dr Nabil El Ioini  
Year: 2025

# [Problem Statement and Motivation]

The problem we aim to solve with our mobile application is the low awareness of, and the lack of easy access to, readily available pet caretakers in situations where a person may need to leave their pets unattended for prolonged periods of time, such as on holidays or work trips. To Expand on these problems, information on petcare specialists are not widely known and it may not be very suitable for a client to contact an unlicensed or impromptu hire with little or no experience in pet-taking. Our app's goal is to address these issues; allowing users to easily browse and learn about local trusted or licensed petcare experts as well as provide assistance regarding both on-site and off-site service options.

This problem which our team has decided to tackle is important because of the lack of support given to worrisome pet owners when it comes to unexpected times such as unplanned leaves causing their pets to be left at home unattended, and the lack of widely available and publicly known services for this kind of situation. Despite the existence of similar apps such as Petbacker.com, there is still a gap in the market for a premium and officially recognised application for pet caretaking. Moreover, this solution is appropriately being developed as a mobile application for the convenience of the user, pet owners and caretakers alike, as it allows them to easily and quickly keep track of their pets situation with direct contact with a hired pet-taker through our app, as well as being able to schedule and browse pet-taking services on-the-go. Mobile applications are also becoming increasingly popular in general for the aforementioned reasons; many web applications transition into a mobile form to expand their audiences and our team has determined this to be largely beneficial for the success of our application.

To discuss other similar apps in the market, it is only fair to mention the biggest competitor, Petbacker.com. This application works very similarly to our proposed solution; an app whose main purpose is to connect pet owners with pet takers. Many features overlap between our mobile application and Petbacker.com, such as the ability to browse pet taker details, information about pet takers near their location, and an on-site or off-site pet taking approach option. As well as this, another forum where similar action is taken to tackle our problem is a Facebook group for petsitting volunteers, with the main difference being that it works on a voluntary basis with no monetisation involved. Pawshake is a site which also aims to provide a solution to the issue of inaccessibility of pet caretakers which primarily focuses on booking and chatting features. However, the app does fail to provide consistent and verifiable updates about petcare updates.

Although these apps successfully address the issues regarding accessible pet caretaking, our app stands out from them with more novel and useful features. Firstly, we plan on collaborating with official pet stores and pet related companies for official and licensed professional pet takers. This step will give our app a stronger, safer and more recognisable foundation for our users. Furthermore, we will implement a premium version for pet taking, where with the utilisation of official connections with pet hotels and other services, we grant pet owners the option for a more luxurious and appealing pet caretaking experience. Of course, users will also be able to partake in pet caretaking by registering as a pet-taker in our app, allowing both licensed and unlicensed caretakers on the platform for a user to decide which is the best option for them. As well as simple pet taking, our app will also allow users to make use of our collaborations with official pet caretaking companies to also allow additional services like grooming, pet food delivery and vet visits. To ensure the safety and legitimacy of pets and caretaker duties, we plan on implementing an in-app camera feature to combat fraudulent acts. This feature can only be used with location services enabled on a mobile device and restricts access to a mobile user's camera roll to prevent using older photos as proof of work. This system will be used to provide caretakers the ability to provide active updates on the pet's activities and condition.


# [Potential Impact and Challenges]

For such an ambitious and large-scale project, there may be challenges which our team may face, from as early as design stage to a fully functional app. In this section we plan to address some of them and provide a possible solution. To begin with, the feature of an in-app camera, which although would not allow direct access when uploading a picture for caretaking updates, will still access the mobile device's disk storage to store the photos taken for that purpose. The more frequent the updates, the more space the photos will take, to an unnecessary amount. Our solution to this is to use image compression before the upload of a photo to the owner and before it's stored on the device's gallery. Moreover, the app will have the ability to automatically delete files and photos which are older than 30 days to keep file management relevant.

The large system of real-time updates between pet caretakers and pet owners will prove to be troublesome to implement and owners would ideally need to be able to receive updates instantly, or at the very least not too late from the time of the actual update. To address this, our team will use Firebase Realtime Database, proven to be widely used and reliable for applications which require it. As well as this, the app will feature the ability to push notifications on mobile devices, so that the owner will be aware of the of any updates and notifications that they may need to see.

Another problem where the previous solution may seem fit is the pet sitter's own forgetfulness; a caretaker may forget to update the owner on their pet's situation following the actual activity, and they may forget to upload a photo for proof. Similarly, our application will have the ability to push reminders linked to checklist tasks to the caretaker to ensure that the task is fulfilled and the pet update proof is successfully uploaded for the owner to be notified of.

To list some Impacts and benefits that our app may provide, our project and application builds trusts and accountability between pet caretakers and pet owners alike through verifiable and non-editable activity logs. photos uploaded by the caretaker are certainly undeniable, and chats between the owner and caretaker allow smooth communication and solid trust building. Our app also encourages responsible pet ownership through the pet care checklist which can be set automatically or by the owner themselves. The pet owner must be aware of all proper routines for pet caretaking for their hired pet caretaker to abide by. On top of this, proper reminders for the caretakers also achieve this. Our app ultimately aims to foster local pet care communities by connecting owners and sitters, whether part-time, licensed or officially employed. Some economic benefits this project may have include creating income opportunities for pet lovers and part-time workers interested in this business, as well as offering cost-effective alternatives to traditional boarding services and pet hotel rental.

# [User Stories]

*“As a [type of user], I want [an action] so that [a benefit/a value].”*

## Pet Owner

### Must-have

> I want to browse for local available pet-takers to determine a good candidate to take care of my pets.

> I want to be able to register for an account so that i may user features of the app such as booking a pet taker etc.

> I want to be able to communicate with the pet-taker i have hired so i can constantly be updated on my pet's situation.

> I want to book a date and time for my pet-taker to come to my house so that my pets will be ready to be taken care of while i'm gone.

> I want to create a care checklist for the pet ccaretaker so that my pet's daily routine is followed properly.

> I want a daily summary report showing the pet caretaker's activities so i can review the day easily.

### Nice-to-have

> I want to be able to view reviews of certaini pet-takers to judge whether they are fit to take care of my pets.

## Pet Taker

### Must-have

> I want to create a pet taking account so that i may be able to take care of pets of owners on the app.

> I want to be able to log my activity as proof for the woner who hired me to know i did the job correctly.

> I want to be able to contact the owner immediately if an emergency occurs so i can get quick assistance.

### Nice-to-have

> I want notifications for people interested in hiring me so that i can know if a have work or not.

> I want to be able to loc care activities without an internet connection so i dont lose progress.

## Administrator

### Must-have

> I want to be able to monitor the accounts registering as a client or pet taker so that i may know if there may be bot or malicious accounts on the platform.

> I want to be able to block, report, ban and remove accounts deemed suspicious so that the app may not be overrun with useless accounts.

> I want to be able to contact and warn user and pet takers alike of their own or other suspicious activities on the app.

> I want to be able to inform all users on the app of any maintenance or scheduled down time so that people may not write bad reviews for the app.

# [Use Cases]

![Use Case Diagram](https://github.com/COMP3040-2025-2026/coursework-1-f/blob/main/UseCaseDiagramV2.jpg)

### Pet Owner

A user wants to be able to register an account or login to their account for the app. With this account, they want to be able to browse through and find information on pet caretakers and services for their pets. Once they find a caretaker that they are interested in hiring, they want to be able to schedule, book and contact them directly for them to do their task. When everything is settled, the user will want to be able to pay the caretaker for their services in the app.

### Pet Caretaker

A caretaker would want to register or login to the system on the app. They would want to be able to fill in information about themselves, where they are, their preferred work hours, and expertise. A caretaker would want to know if a pet owner has contacted them and are interested in hiring them and to be able to contact them during their time working.

### Administrator

An administrator would want a secure login page that cannot easily be broken into so that they may do their administrative jobs without risk of any malicious users. They would want the ability to remove and malicious users, pet owners and caretakers alike and remove any comments which may be deemed harmful. The admin should be able to contact and inform all users of any information relating to the system, app or warning users of certain threats or of their own behaviours as well. 

### Vet Clinic

A vet clinic would want to be able to check any bookings made by pet owners or sitters uing the app, including details such as the date and time and what type of services. They should also be able to communicate with both pet owners and sitter in the case of any emergencies or just normal treatments or pet check ups.

# [UI Wireframe]

### Starting Page
![wireframe - starting page](https://github.com/COMP3040-2025-2026/coursework-1-f/blob/main/Starting%20UI.png)

### Pet Sitter UI
![wireframe - pet sitter UI](https://github.com/COMP3040-2025-2026/coursework-1-f/blob/main/Pet%20Sitter%20UI.png)

### Pet Owner UI
![wireframe - pet owner UI](https://github.com/COMP3040-2025-2026/coursework-1-f/blob/main/PetOwner%20UI.png)

Higher Fidelity Image can be found [here](https://github.com/COMP3040-2025-2026/coursework-1-f/blob/main/CareTail%20Wireframe.pdf)
