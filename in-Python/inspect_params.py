import firebase_functions
import firebase_functions.params as params

print('firebase_functions version', getattr(firebase_functions, '__version__', 'unknown'))
print('params module', params)
print([name for name in dir(params) if 'param' in name.lower() or 'string' in name.lower()][:100])
