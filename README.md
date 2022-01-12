# Annotations integration demo

Example of a communication process using Spring Integration components based on annotations.

The same demo using DSL, can be found on repository [Dsl Demo](https://github.com/carlosggz/DslIntegrationDemo).

The application reads a mail server every few seconds, and process the arrived mails according to the subject:
- "Confirmation - [number]", will be sent to a rabbit exchange (app creates automatically the exchange and a binding queue to store the messages).
- "Cancellation - [number]", will be sent to a ftp folder (the uploads folder is created locally under the docker folder).
- "Info Required - [number]", will be sent to a http service.
- Any other topic, will be sent to a custom service.

The workflow is described on the following diagram:

![workflow](./docs/activity.png)

The demo illustrates the following components:
- Pop3 inbound channel
- Ftp outbound channel
- RabbitMq outbound channel
- Http outbound channel
- Channel interceptor
- Channel router
- Message transformer
- Message handler chain
- Messaging gateway
- Service activator
- Direct and PublishSubscribeChannel channel
- Testing support

The components and the interaction are described on the following diagram:

![components](./docs/components.png)

## Instructions

You can provide your own infrastructure or use the one provided on the docker composer file. In any case you must provide your pop3 credentials, or change the faker component settings to use the fake inbound mail component (by default enabled).


    app:
        ...
        faker:
            enable: true

If you use the provided infrastructure, you can check:
- confirmed messages in the queue at http://localhost:15672/#/queues/%2F/queue.demo. Queue will be automatically created by the application.
- cancelled messages in the folder src/docker/uploads of the project.
- information and invalid messages  will be only available on the log file at build/logs/my.log 

All messages processed can be retrieved on local database.