import json


class SerializeJson:
    def serialize(data):
        return json.dumps(data)

    def deserialize(json_string):
        return json.loads(json_string)
