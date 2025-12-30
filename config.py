import os

basedir = os.path.abspath(os.path.dirname(__file__))


class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'dev-secret-key-change-in-production'
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or \
        'sqlite:///' + os.path.join(basedir, 'cooking.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    # Suriname Dollar as default currency
    DEFAULT_CURRENCY = 'SRD'
    CURRENCY_SYMBOL = 'SRD'
